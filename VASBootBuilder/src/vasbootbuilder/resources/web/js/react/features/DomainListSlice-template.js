#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getRequest, postRequest, putRequest } from "../../utils/authority";
import { first } from "lodash";

const initialState = {
  entities: [],
  totalRecords: 0,
  first: 0,
  page: 0,
  perPage: 10,
  filters: {},
  sortField: "",
  sortOrder: "",
};

const ${domainConstantName}S_URI = "${domainObjectName}s";
export const fetch${domainClassName}s = createAsyncThunk(
  "${domainObjectName}s/fetch${domainClassName}s",
  async (na, extra) => {
    const { page, perPage, sortField, sortOrder, filters } =
      extra.getState().${domainObjectName}s;
    const data = await postRequest(
      ${domainConstantName}S_URI +
        `?page=${
          page + 1
        }&per_page=${perPage}&sort_by=${sortField}&order=${sortOrder}`,
      filters
    );
    return data;
  }
);

export const ${domainObjectName}sSlice = createSlice({
  name: "${domainObjectName}s",
  initialState: initialState,
  reducers: {
    pageChanged: (state, action) => {
      console.log("Cahgne page ", action.payload);
      state.first = action.payload.first;
      state.page = action.payload.page;
    },
    sorted: (state, action) => {
      state.sortField = action.payload.sortField;
      state.sortOrder = action.payload.sortOrder;
    },
    filterEdited: (state, action) => {
      state.filters[action.payload.name] = action.payload.value;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetch${domainClassName}s.pending, (state) => {
        state.status = "loading";
      })
      .addCase(fetch${domainClassName}s.fulfilled, (state, action) => {
        state.entities = action.payload.rows;
        state.totalRecords = action.payload.totalRecords;
        state.page = action.payload.currentPage;
        state.status = "done";
      });
  },
});

export const { pageChanged, sorted, filterEdited } = ${domainObjectName}sSlice.actions;
export default ${domainObjectName}sSlice.reducer;
