<template>
  <div class="links-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>链路列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleOpenDialog()" :icon="Plus">新增链路</el-button>
            <el-dropdown @command="handleExport">
              <el-button type="success">
                导出<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="current">导出当前</el-dropdown-item>
                  <el-dropdown-item command="all">导出全部</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="searchForm" label-position="right" label-width="80px">
        <el-row :gutter="10" class="search-row">
          <el-col :span="4">
            <el-form-item label="链路等级">
              <el-select v-model="searchForm.linkType" placeholder="请选择" clearable @change="handleSearch" style="width: 100%;">
                <el-option label="一级链路" value="PRIMARY"></el-option>
                <el-option label="多级链路" value="SECONDARY"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="IP地址">
              <el-input v-model="searchForm.ipAddress" placeholder="IP" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="跳转地区">
              <el-input v-model="searchForm.regionPath" placeholder="地区" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="VPS商家">
              <el-input v-model="searchForm.vpsProvider" placeholder="商家" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="采购者">
              <el-input v-model="searchForm.purchaser" placeholder="采购者" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10" class="search-row">
          <el-col :span="9">
            <el-form-item label="购买时间">
              <el-date-picker v-model="searchForm.purchaseDateRange" type="daterange" range-separator="-" start-placeholder="购买开始" end-placeholder="购买结束" @change="handleSearch" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="9">
            <el-form-item label="到期时间">
              <el-date-picker v-model="searchForm.expireDateRange" type="daterange" range-separator="-" start-placeholder="到期开始" end-placeholder="到期结束" @change="handleSearch" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="6" style="text-align: right;">
            <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
            <el-button @click="handleReset" :icon="Refresh">重置</el-button>
          </el-col>
        </el-row>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="links" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="linkType" label="链路等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.linkType === 'PRIMARY' ? 'success' : 'warning'">
              {{ row.linkType === 'PRIMARY' ? '一级链路' : '多级链路' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="regionPath" label="跳转地区" width="180" />
        <el-table-column prop="ipAddress" label="IP地址" width="130">
          <template #default="{ row }">
            <el-tag type="primary" size="small" v-if="row.ipAddress">{{ row.ipAddress }}</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
         <el-table-column prop="ipAddress2" label="多级IP" width="130">
           <template #default="{ row }">
             <div v-if="row.ipAddress2" class="ip-tags-container">
               <el-tag 
                 v-for="ip in row.ipAddress2.split(',')" 
                 :key="ip" 
                 type="info" 
                 size="small"
               >
                 {{ ip.trim() }}
               </el-tag>
             </div>
             <span v-else class="text-muted">-</span>
           </template>
         </el-table-column>
        <el-table-column prop="jumpCount" label="跳转次数" width="100" />
        <el-table-column prop="vpsProvider" label="VPS商家" width="120" />
        <el-table-column prop="purchaser" label="采购者" width="100" />
        <el-table-column prop="price" label="价格(元)" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="purchaseTime" label="购买时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.purchaseTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="到期时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.expireTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleOpenDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        style="margin-top: 20px;"
        :current-page="pagination.currentPage"
        :page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑链路' : '新增链路'" width="600px" @close="handleCloseDialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="跳转次数" prop="jumpCount">
              <el-input-number v-model="form.jumpCount" :min="1" :max="4" @change="handleJumpCountChange" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="链路等级" prop="linkType">
              <el-select v-model="form.linkType" placeholder="请选择链路等级" disabled>
                <el-option label="一级链路" value="PRIMARY" />
                <el-option label="多级链路" value="SECONDARY" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="IP地址" prop="ipAddress">
              <el-input v-model="form.ipAddress" placeholder="请输入IP地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="多级IP" prop="ipAddress2">
              <el-input 
                v-model="form.ipAddress2" 
                type="textarea"
                :rows="2"
                placeholder="请输入多级IP地址，用逗号(,)分隔" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="跳转地区" prop="regionPath">
          <el-input v-model="form.regionPath" placeholder="例如：北京-东京-洛杉矶" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="采购者" prop="purchaser">
              <el-input v-model="form.purchaser" placeholder="请输入采购者" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="VPS商家" prop="vpsProvider">
              <el-input v-model="form.vpsProvider" placeholder="请输入VPS商家" />
            </el-form-item>
          </el-col>
        </el-row>
         <el-row>
          <el-col :span="12">
            <el-form-item label="购买时间" prop="purchaseTime">
              <el-date-picker
                v-model="form.purchaseTime"
                type="datetime"
                placeholder="选择购买时间"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期时间" prop="expireTime">
              <el-date-picker
                v-model="form.expireTime"
                type="datetime"
                placeholder="选择到期时间"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="价格" prop="price">
          <el-input v-model="form.price" placeholder="请输入价格" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getLinkList, createLink, updateLink, deleteLink, exportLinks } from '@/api/link';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Search, Refresh, ArrowDown } from '@element-plus/icons-vue';

const links = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

const searchForm = reactive({
  linkType: '',
  ipAddress: '',
  regionPath: '',
  vpsProvider: '',
  purchaser: '',
  purchaseDateRange: null,
  expireDateRange: null,
});

const formRef = ref(null);
const form = reactive({
  id: null,
  linkType: 'PRIMARY',
  jumpCount: 1,
  ipAddress: '',
  ipAddress2: '',
  regionPath: '',
  purchaser: '',
  vpsProvider: '',
  purchaseTime: '',
  expireTime: '',
  price: ''
});

const rules = {
  linkType: [{ required: true, message: '请选择链路等级', trigger: 'change' }],
  jumpCount: [{ required: true, message: '请输入跳转次数', trigger: 'blur' }],
  ipAddress: [
    { required: true, message: '请输入IP地址', trigger: 'blur' },
    { pattern: /^((\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/, message: '请输入合法的IP地址', trigger: 'blur' }
  ],
  ipAddress2: [
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          // 可选字段，为空时直接通过
          return callback();
        }
        const ips = value.split(',');
        const ipRegex = /^((\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
        for (const ip of ips) {
          if (!ipRegex.test(ip.trim())) {
            return callback(new Error(`IP地址 [${ip.trim()}] 格式不正确`));
          }
        }
        callback();
      }, 
      trigger: 'blur' 
    }
  ],
  regionPath: [{ required: true, message: '请输入跳转地区', trigger: 'blur' }],
  purchaser: [{ required: true, message: '请输入采购者', trigger: 'blur' }],
  vpsProvider: [{ required: true, message: '请输入VPS商家', trigger: 'blur' }],
  purchaseTime: [{ required: true, message: '请选择购买时间', trigger: 'change' }],
  expireTime: [{ required: true, message: '请选择到期时间', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
};

const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString();
};

const getLinks = async () => {
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      linkType: searchForm.linkType,
      ipAddress: searchForm.ipAddress,
      regionPath: searchForm.regionPath,
      vpsProvider: searchForm.vpsProvider,
      purchaser: searchForm.purchaser,
      purchaseTimeStart: searchForm.purchaseDateRange ? searchForm.purchaseDateRange[0] : null,
      purchaseTimeEnd: searchForm.purchaseDateRange ? searchForm.purchaseDateRange[1] : null,
      expireTimeStart: searchForm.expireDateRange ? searchForm.expireDateRange[0] : null,
      expireTimeEnd: searchForm.expireDateRange ? searchForm.expireDateRange[1] : null,
    };
    loading.value = true;
    const res = await getLinkList(params);
    links.value = res.data.content;
    pagination.total = res.data.totalElements;
    pagination.currentPage = res.data.number + 1;
  } catch (error) {
    ElMessage.error('获取链路列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  getLinks();
};

const handleReset = () => {
  searchForm.linkType = '';
  searchForm.ipAddress = '';
  searchForm.regionPath = '';
  searchForm.vpsProvider = '';
  searchForm.purchaser = '';
  searchForm.purchaseDateRange = null;
  searchForm.expireDateRange = null;
  handleSearch();
};

const handleSizeChange = (size) => {
  pagination.pageSize = size;
  getLinks();
};

const handleCurrentChange = (page) => {
  pagination.currentPage = page;
  getLinks();
};

const handleOpenDialog = (row = null) => {
  dialogVisible.value = true;
  if (row) {
    Object.assign(form, row);
  } else {
    // Reset form for new entry
    Object.assign(form, {
      id: null,
      linkType: 'PRIMARY',
      jumpCount: 1,
      ipAddress: '',
      ipAddress2: '',
      regionPath: '',
      purchaser: '',
      vpsProvider: '',
      purchaseTime: '',
      expireTime: '',
      price: ''
    });
  }
};

const handleCloseDialog = () => {
  formRef.value.resetFields();
};

const handleJumpCountChange = (count) => {
  if (count === 1) {
    form.linkType = 'PRIMARY';
  } else if (count > 1) {
    form.linkType = 'SECONDARY';
  }
};

const handleSubmit = async () => {
  try {
    const valid = await formRef.value.validate();
    if (!valid) return;

    // 提交前根据跳转次数自动设置链路等级
    handleJumpCountChange(form.jumpCount);

    if (form.id) {
      await updateLink(form.id, form);
      ElMessage.success('更新成功');
    } else {
      await createLink(form);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    getLinks();
  } catch (error) {
    // ElMessage.error(form.id ? '更新失败' : '新增失败');
  }
};

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条链路吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await deleteLink(id);
    ElMessage.success('删除成功');
    getLinks();
  }).catch(() => {
    // catch cancel
  });
};

const handleExport = (command) => {
  ElMessage.info('正在生成导出文件，请稍候...');
  const params = {
    exportMode: command,
    // Add other filters only if exporting current view
    ...(command === 'current' && {
      linkType: searchForm.linkType,
      ipAddress: searchForm.ipAddress,
      regionPath: searchForm.regionPath,
      vpsProvider: searchForm.vpsProvider,
      purchaser: searchForm.purchaser,
      purchaseTimeStart: searchForm.purchaseDateRange ? searchForm.purchaseDateRange[0] : null,
      purchaseTimeEnd: searchForm.purchaseDateRange ? searchForm.purchaseDateRange[1] : null,
      expireTimeStart: searchForm.expireDateRange ? searchForm.expireDateRange[0] : null,
      expireTimeEnd: searchForm.expireDateRange ? searchForm.expireDateRange[1] : null,
    }),
  };
  exportLinks(params);
};

onMounted(() => {
  getLinks();
});
</script>

<style scoped>
.links-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-actions {
  display: flex;
  gap: 10px;
}
.search-form-inline {
  display: flex;
  flex-wrap: nowrap;
  gap: 15px;
  align-items: center;
}
.search-form-inline .el-form-item {
  margin-bottom: 0; /* Override default margin */
}
.search-form-inline .action-buttons {
  margin-left: auto;
  flex-shrink: 0;
}
.search-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
.search-row .el-form-item {
  margin-bottom: 0; /* Override default margin for items in a row */
}
.text-muted {
  color: #909399;
}
.ip-tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
</style>