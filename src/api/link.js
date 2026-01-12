import request from './request'

export function getLinkList(params) {
  return request({
    url: '/links',
    method: 'get',
    params
  })
}

export function getLinkById(id) {
  return request({
    url: `/links/${id}`,
    method: 'get'
  })
}

export function createLink(data) {
  return request({
    url: '/links',
    method: 'post',
    data
  })
}

export function updateLink(id, data) {
  return request({
    url: `/links/${id}`,
    method: 'put',
    data
  })
}

export function deleteLink(id) {
  return request({
    url: `/links/${id}`,
    method: 'delete'
  })
}

export function getLinkStats() {
  return request({
    url: '/links/stats',
    method: 'get'
  })
}

export function exportLinks(params) {
  // We don't expect a JSON response, so we handle the blob directly
  return request({
    url: '/links/export',
    method: 'get',
    params,
    responseType: 'blob' 
  }).then(response => {
    const blob = new Blob([response], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `链路数据_${new Date().getTime()}.csv`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(link.href);
  }).catch(err => {
    console.error("导出失败:", err);
    throw err; // Re-throw the error to be caught by the caller
  });
} 