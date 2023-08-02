<template>


  <div class="common-layout">
    <el-container>
      <el-main>
        <router-view/>
      </el-main>
      <el-footer>作者：
        <el-link :underline="false"
                 type="primary"
                 target="_blank"
                 href="https://github.com/Jinglong233">
          JingLong&emsp;
        </el-link>
        <el-link :underline="false" type="primary" target="_blank"
                 href="https://github.com/Jinglong233/JSON_EXTRACT_SQL">
          <el-icon style="font-size: 20px" class="iconfont icon-github"></el-icon>
          github已开源，欢迎star
        </el-link>
      </el-footer>
    </el-container>
  </div>

</template>

<style scoped>

html, body {
  padding: 0;
  margin: 0;
}

.el-main {
  padding: 0;
  margin: 0;
}

.el-footer {
  height: 50px;
  line-height: 50px;
  text-align: center;
  font-size: 15px;
  color: #494a4c;
  bottom: 0;
}
</style>
