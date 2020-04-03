import request from '@/utils/request'

export function listRule(query) {
  return request({
    url: '/userProperty/listRule',
    method: 'get',
    params: query
  })
}

export function listScore(query) {
  return request({
    url: '/userProperty/listScore',
    method: 'get',
    params: query
  })
}

export function updateRule(data) {
  return request({
    url: '/userProperty/updateRule',
    method: 'post',
    data
  })
}

