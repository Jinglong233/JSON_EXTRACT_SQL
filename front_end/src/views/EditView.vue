<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <el-row style="line-height: 60px">
          <el-col :span="1" style="text-align: center;font-size: 28px;">
            <el-icon>
              <Tools/>
            </el-icon>
          </el-col>
          <el-col :span="5" style="text-align: center;">
            <el-text tag="b" style="font-size: 28px;">JSON提取SQL工具</el-text>
          </el-col>
          <el-col :span="8">
          </el-col>
          <el-col :span="10">
            <el-button type="primary" @click="openDialog" size="large">生成SQL</el-button>
          </el-col>
        </el-row>

      </el-header>
      <el-main>
        <el-row :gutter="0">
          <el-col :span="10">
            <JsonEditorVue
                ref="edit"
                class="editor"
                v-model="data"
                :modeList="['tree', 'code', 'form', 'view']"
            />
          </el-col>
          <el-col :span="14">
            <codemirror-editor class="editor" :data="resultData"/>
          </el-col>
        </el-row>
      </el-main>
      <!--对话框-->
      <el-dialog
          v-model="dialogVisible"
          title="SQL表单"
          width="30%"
          align-center
          @close="closeDialog"
      >
        <template #default>
          <el-form
              ref="ruleFormRef"
              :model="sqlFrom"
              status-icon
              :rules="rules"
              label-width="120px"
              class="demo-ruleForm"
          >
            <el-form-item label="表名称" prop="tableName">
              <el-input v-model.trim="sqlFrom.tableName" type="text" autocomplete="off" placeholder="请输入表名称"/>
            </el-form-item>
            <el-form-item label="Key" prop="key">
              <el-select v-model="sqlFrom.key"
                         class="m-2"
                         placeholder="选择Key"
                         size="large"
                         @change="keyChange">
                <el-option
                    v-for="item in optionsList"
                    :key="item"
                    :label="item"
                    :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="removeKeys" prop="pass">
              <el-select
                  v-if="childOptions.length===0"
                  v-model="sqlFrom.removeList"
                  multiple
                  collapse-tags
                  placeholder="无子key"
                  style="width: 240px"
              >
                <el-option
                    v-for="item in childOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                />
              </el-select>
              <el-select
                  v-if="childOptions.length!==0"
                  v-model="sqlFrom.removeList"
                  multiple
                  collapse-tags
                  placeholder="选择需要去除的子Key"
                  style="width: 240px"
              >
                <el-option
                    v-for="item in childOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="type" prop="type">
              <el-select v-model="sqlFrom.type" class="m-2" placeholder="选择生成类型" size="large">
                <el-option label="UPDATE" value="UPDATE"/>
                <el-option label="INSERT" value="INSERT"/>
              </el-select>
            </el-form-item>
            <el-form-item label="新增字段" prop="statement">
              <el-input v-model.trim="sqlFrom.statement" type="text" autocomplete="off"/>
            </el-form-item>
            <el-form-item label="replaceList" prop="pass">
              <el-tag
                  v-for="attr in sqlFrom.replaceList"
                  :key="attr"
                  class="mx-1"
                  closable
                  :disable-transitions="false"
                  @close="handleClose(attr)"
              >
                {{ attr }}
              </el-tag>
              <el-input
                  v-if="inputVisible"
                  ref="InputRef"
                  v-model.trim="inputValue"
                  class="ml-1 w-20"
                  size="small"
                  @keyup.enter="handleInputConfirm"
                  @blur="handleInputConfirm"
              />
              <el-button v-else class="button-new-tag ml-1" size="small" @click="showInput">
                + 新增
              </el-button>
            </el-form-item>
          </el-form>
        </template>
        <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sub">
          生成
        </el-button>
      </span>
        </template>
      </el-dialog>
    </el-container>
  </div>


</template>

<script setup>
import JsonEditorVue from 'json-editor-vue3'
import {nextTick, ref, toRaw} from "vue";
import {getChildKeys, getObjKeys, getVal} from "@/api/Json";
import {ElMessage} from "element-plus";
import {Tools} from "@element-plus/icons-vue";
import CodemirrorEditor from "@/components/CodemirrorEditor.vue";

const data = ref({
  "employees": [{
    "firstName": "Bill",
    "lastName": "Gates"
  }, {
    "firstName": "George",
    "lastName": "Bush"
  }, {
    "firstName": "Thomas",
    "lastName": "Carter"
  }]
})
const resultData = ref()
const edit = ref({});
const optionsList = ref([]);
const dialogVisible = ref(false);
const sqlFrom = ref({
  replaceList: [],
  removeList: []
});
// 标签相关
const inputVisible = ref(false);
const inputValue = ref();
const InputRef = ref();
// 子key列表
const childOptions = ref([]);
// 表单对象
const ruleFormRef = ref();


const sub = async () => {
  await ruleFormRef.value.validate(async (vaild) => {
    if (vaild) {
      let booleanPromise = await isValidateJson();
      if (booleanPromise) {
        const res = await getVal(toRaw(data.value), sqlFrom.value);
        if (res.code === 0) {
          resultData.value = res.data.join('\n');
          ElMessage({
            message: '生成SQL成功',
            type: 'success'
          })
          dialogVisible.value = false;
        } else {
          ElMessage({
            message: res.message,
            type: 'error',
          })
        }
      } else {
        ElMessage({
          message: 'json格式非法',
          type: 'error'
        })
      }

    }
  })
}
/**
 * 获取所有key
 */
const getKeys = async () => {
  await getObjKeys(data.value).then(res => {
    if (res.code === 0) {
      /* ElMessage({
         message: '提取成功',
         type: 'success'
       })*/
      optionsList.value = Array.from(new Set(res.data));
    }
  }).catch(error => {
    console.log(error);
  })
}


/**
 * 判断json是否合法
 * @returns {Promise<boolean>}
 */
const isValidateJson = async () => {
  let editorInstance = await toRaw(edit.value.editor);
  // console.log("editorInstance", editorInstance);
  const res = await editorInstance.validate();
  return res.length === 0;
}

/**
 * 新增标签（显示Input框）
 */
const showInput = () => {
  inputVisible.value = true;
  nextTick(() => {
    InputRef.value.focus();
  })
}

/**
 * 当keySelect改变的时候，请求其对应的子options
 */
const keyChange = async () => {
  const res = await getChildKeys(data.value, sqlFrom.value.key);
  if (res.code === 0) {
    childOptions.value = res.data;
  } else {
    ElMessage({
      message: '获取childKey失败',
      type: 'error'
    })
  }
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    sqlFrom.value.replaceList.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}
const handleClose = (attr) => {
  sqlFrom.value.replaceList.splice(sqlFrom.value.replaceList.indexOf(attr), 1)
}

/**
 * 校验规则
 */
const rules = ref({
  tableName: [
    {required: true, message: '表名称不能为空', trigger: 'blur'},
    {min: 1, max: 32, message: '0<名称长度<=32', trigger: 'blur'},
  ],
  key: [
    {required: true, message: 'key不能为空', trigger: 'change'}
  ],
  statement: [
    {max: 200, message: '额外字段总长度<=200', trigger: 'blur'},
  ],
  type: [
    {required: true, message: 'SQL类型不能为空', trigger: 'change'}
  ],
})

/**
 * 用于清空对象中的属性值
 * @param obj
 */
const clearValue = (obj) => {
  Object.keys(obj).forEach(key => {
    if (typeof obj[key] == 'object') {
      clearValue(obj[key])
    } else {
      obj[key] = '';
    }
  });
}


/**
 * 打开对话框
 */
const openDialog = async () => {
  await getKeys();
  dialogVisible.value = true;
  // 清除表单的所有的检验状态
  ruleFormRef.value.clearValidate();
}


/**
 * 关闭对话框
 */
const closeDialog = () => {
  // 关闭对话框
  dialogVisible.value = false;
  // 清空表单对象中的所有属性值
  clearValue(sqlFrom.value);
  // 清空select下拉框的值
  childOptions.value = [];
  sqlFrom.value.removeList = [];
  // 清空arrtList
  sqlFrom.value.replaceList = [];
}

</script>

<style scoped>
.editor {
  height: 600px;
}

.el-main {
  padding: 0;
  margin: 0;
}

</style>