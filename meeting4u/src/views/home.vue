<template>
  <div class="c_home">
    <el-container>
      <el-header class="c_header">
        <div class="c_header_left">
          <router-link to="/">
            <span style="color: white; font-size: 25px; letter-spacing: 2px;position: relative;">
              <img src="../assets/meeting.png" style="position: absolute;left: -40px;">
              南苑最好用的会议系统 
            </span>
          </router-link>
        </div>
        <div class="c_header_right" v-if="logined">
          <span
            style="
              color: white;
              font-size: 15px;
              letter-spacing: 2px;
              margin-right: 10px;">{{ username}}</span>
          <router-link to="/">
            <a class="c_header_logout"
              ><span
                @click="logout"
                style="color: white; font-size: 15px; letter-spacing: 2px"
                >登出</span
              ></a
            >
          </router-link>
        </div>
        <div class="c_header_right" v-else></div>
      </el-header>
      <el-main class="c_main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>
  <script>
export default {
  name: "HomePage",
  data() {
    return {
      logined:false,
      username:''
    };
  },
  mounted(){
    this.logined = window.sessionStorage.getItem('logined');
    this.username = window.sessionStorage.getItem('username');
  },
  methods: {
    logout() {
      this.$axios
        .post(this.$meetingServerURL + "/logout")
        .then(async (res) => {
          if (res.code == 200) {
            this.logined = true;
            window.sessionStorage.removeItem("token");
            window.sessionStorage.removeItem("username");
            window.sessionStorage.removeItem("logined");
            this.$router.go(0);
          } else {
            this.$message.error(res.msg);
          }
        })
        .catch((err) => {
          this.$message.error(err);
        });
    },
  },
};
</script>
  
  <style scoped>
.router-link-active {
  text-decoration: none;
}
.c_header {
  background-color: dodgerblue;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.c_header_left {
  margin-left: 50px;
}
.c_header_logout:hover {
  cursor: pointer;
  text-decoration: underline;
}
.c_main {
  height: 90vh;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.c_mainbox {
  margin: 40px 30px 20px 30px;
  display: flex;
  height: 80vh;
}
.c_mainleft {
  width: 30%;
  display: inline;
  padding-top: 30px;
  margin: 0, auto;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.c_mainright {
  width: 70%;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.c_main_row {
  margin-bottom: 40px;
}
.c_main_room {
  color: dodgerblue;
  padding: 10px 0;
  background-color: white;
  width: 60%;
  font-size: 20px;
  border: none;
  border-bottom: 2px solid black;
}
.c_login_input {
  padding: 10px 0;
  background-color: white;
  width: 60%;
  font-size: 20px;
}
.c_main_room:focus {
  border: 0;
  border-bottom: 3px solid #232931;
  border-top: 2px solid white;
  outline: none;
}
.c_main_join {
  margin-top: 15px;
  margin-left: 44%;
  font-size: 22px;
}
.c_main_join:hover {
  cursor: pointer;
  text-decoration: underline;
}
.roomcode-error {
  border-bottom: 2px solid #fa1515;
}
.c_mainleft .c_main_row .el-button {
  width: 60%;
  font-size: 20px;
}
.c_video {
  width: 100%;
  height: 70vh;
  background-color: #f4f7fa;
  border-radius: 10px;
  object-fit: cover;
}

.c_video_settings {
  margin-top: 20px;
}
</style>