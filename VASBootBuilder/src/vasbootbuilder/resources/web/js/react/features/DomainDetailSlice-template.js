#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

import { createAsyncThunk, createSlice, current } from "@reduxjs/toolkit";
import { getRequest, postRequest, putRequest } from "../../utils/authority";

const initialState = {
  entity: { ${domainObjectName}Id: 0, ${domainObjectName}Name: "", ${domainObjectName}Balance: "" },
};

export const fetch${domainClassName} = createAsyncThunk(
  "${domainObjectName}/fetch${domainClassName}",
  async (url) => {
    const data = await getRequest(url);
    return data;
  }
);

const ${domainConstantName}_SAVE_URI = "${domainObjectName}";
export const save${domainClassName} = createAsyncThunk(
  "${domainObjectName}/save${domainClassName}",
  async (${domainObjectName}) => {
    const data =
      ${domainObjectName}.${domainObjectName}Id === ""
        ? await postRequest(${domainConstantName}_SAVE_URI, ${domainObjectName})
        : await putRequest(${domainConstantName}_SAVE_URI + "/" + ${domainObjectName}.${domainObjectName}Id, ${domainObjectName});
    return data;
  }
);

export const ${domainObjectName}Slice = createSlice({
  name: "${domainObjectName}",
  initialState,
  reducers: {
    edited: (state, action) => {
      state.entity[action.payload.name] = action.payload.value;
    },
    created: (state, action) => {
      state.entity = initialState.entity;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetch${domainClassName}.pending, (state) => {
        state.status = "loading";
      })
      .addCase(fetch${domainClassName}.fulfilled, (state, action) => {
        state.entity = { ...current(state.entity), ...action.payload };
        state.status = "done";
      })
      .addCase(save${domainClassName}.pending, (state) => {
        state.status = "loading";
      })
      .addCase(save${domainClassName}.fulfilled, (state, action) => {
        state.entity = action.payload;
        state.status = "done";
      });
  },
});

export const { edited, created } = ${domainObjectName}Slice.actions;
export default ${domainObjectName}Slice.reducer;
