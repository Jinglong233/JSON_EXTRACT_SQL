<template>
  <el-container>
    <el-header>
      <el-button v-if="props.data!==undefined"
                 style="float: right;margin-right: 50px"
                 type="primary"
                 @click="saveToFile">
        保存
      </el-button>
    </el-header>
    <el-main style="border: 1px solid #3883fa;border-left: 0">
      <Codemirror
          v-model:value="props.data"
          :options="cmOptions"
          ref="cmRef"
          @change="onChange"
          @input="onInput"
          @ready="onReady"
      >
      </Codemirror>
    </el-main>
  </el-container>
</template>
<script setup>
import {onMounted, onUnmounted, ref} from "vue"
import "codemirror/mode/sql/sql"
import "codemirror/theme/eclipse.css"
import Codemirror from "codemirror-editor-vue3"

const props = defineProps({
  data: ''
});
const cmRef = ref()
const cmOptions = {
  mode: "text/x-sql",
  theme: 'eclipse', //对应引入的主题
  readOnly: true

}


/**
 * 保存文件到本地
 */
const saveToFile = () => {
  console.log("props", props.data)
  const blob = new Blob([props.data], {type: 'text/plain'});
  const fileName = 'data.sql';
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = fileName;
  link.click();
  URL.revokeObjectURL(link.href);
}
const onChange = (val, cm) => {
  // console.log(val);
}

const onInput = (val) => {
  /*  console.log(val)*/
}

const onReady = (cm) => {
  console.log(cm.focus())
}

onMounted(() => {
  /*  setTimeout(() => {
      cmRef.value?.refresh()
    }, 1000)

    setTimeout(() => {
      cmRef.value?.resize(300, 200)
    }, 2000)

    setTimeout(() => {
      cmRef.value?.cminstance.isClean()
    }, 3000)*/
})

onUnmounted(() => {
  cmRef.value?.destroy()
})
</script>

<style scoped>
main {
  padding: 0;
  margin: 0;
  overflow: hidden;
}

header {
  background-color: #3883fa;
  height: 36px;
  padding: 0;
  margin: 0;
}
</style>