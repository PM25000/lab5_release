/* eslint-disable */
// 该文件由 OneAPI 自动生成，请勿手动修改！
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /api/v1/queryUserList */
export async function queryUserList(
  params: {
    // query
    /** keyword */
    keyword?: string;
    /** current */
    current?: number;
    /** pageSize */
    pageSize?: number;
  },
  options?: { [key: string]: any },
) {
  return request<API.Result_PageInfo_UserInfo__>('/api/v1/queryUserList', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /api/v1/user */
export async function addUser(
  body?: API.UserInfoVO,
  options?: { [key: string]: any },
) {
  return request<API.Result_UserInfo_>('/api/v1/user', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /api/v1/user/${param0} */
export async function getUserDetail(
  params: {
    // path
    /** userId */
    userId?: string;
  },
  options?: { [key: string]: any },
) {
  const { userId: param0 } = params;
  return request<API.Result_UserInfo_>(`/api/v1/user/${param0}`, {
    method: 'GET',
    params: { ...params },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /api/v1/user/${param0} */
export async function modifyUser(
  params: {
    // path
    /** userId */
    userId?: string;
  },
  body?: API.UserInfoVO,
  options?: { [key: string]: any },
) {
  const { userId: param0 } = params;
  return request<API.Result_UserInfo_>(`/api/v1/user/${param0}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    params: { ...params },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /api/v1/user/${param0} */
export async function deleteUser(
  params: {
    // path
    /** userId */
    userId?: string;
  },
  options?: { [key: string]: any },
) {
  const { userId: param0 } = params;
  return request<API.Result_string_>(`/api/v1/user/${param0}`, {
    method: 'DELETE',
    params: { ...params },
    ...(options || {}),
  });
}

export async function showCards(
  params: {
    // path
    /** userId */
    userId?: string;
  },
  options?: { [key: string]: any },
) {
  let res = await request<API.Result_UserInfo_>(`/api/showCards`, {
    method: 'GET',
    params: { ...params },
    ...(options || {}),
  });
  console.log(res);
  return res;
}

export async function addCard(
  body?: API.CardInfo,
  options?: { [key: string]: any },
) {
  return request<API.Result_UserInfo_>('/api/registerCard', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

export async function deleteCard(
  body?: number,
  options?: { [key: string]: any },
) {
  return request<API.Result_UserInfo_>('/api/removeCard', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

export async function showBorrowHistory(
  body?: number,
  options?: { [key: string]: any },
) {
  return request<API.Result_UserInfo_>('/api/showBorrowHistory', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

export async function queryBook(
  body?: API.BookQueryInfo,
  options?: { [key: string]: any },
) {
  let args: any = body;
  if (body?.publishYear != null) {
    if (body!.publishYear[0] != undefined)
      args = { ...args, minPublishYear: args!.publishYear[0] };
    if (body!.publishYear[1] != undefined)
      args = { ...args, maxPublishYear: args!.publishYear[1] };
  }
  if (body?.price != null) {
    if (body!.price[0] != undefined)
      args = { ...args, minPrice: args!.price[0] };
    if (body!.price[1] != undefined)
      args = { ...args, maxPrice: args!.price[1] };
  }
  console.log(args);
  return request<API.Result_UserInfo_>('/api/queryBook', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: args,
    ...(options || {}),
  });
}

export async function addBook(
  body?: API.BookInfo,
  options?: { [key: string]: any },
) {
  return request<API.Result_UserInfo_>('/api/storeBook', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

export async function deleteBook(
  body?: number,
  options?: { [key: string]: any },
) {
  return request<API.Result_UserInfo_>('/api/removeBook', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

export async function modifyBookStock(
  body?: number[], // 0: bookId, 1: deltaStock
  options?: { [key: string]: any },
) {
  return request<API.Result_UserInfo_>('/api/incBookStock', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

export async function modifyBookInfo(
  body?: API.BookInfo,
  options?: { [key: string]: any },
) {
  return request<API.Result_UserInfo_>('/api/modifyBookInfo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

export async function borrowBook(body?: any, options?: { [key: string]: any }) {
  return request<API.Result_UserInfo_>('/api/borrowBook', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

export async function returnBook(body?: any, options?: { [key: string]: any }) {
  return request<API.Result_UserInfo_>('/api/returnBook', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

export async function addBooks(body?: any, options?: { [key: string]: any }) {
  console.log(body);
  return request<API.Result_UserInfo_>('/api/batchStoreBook', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
