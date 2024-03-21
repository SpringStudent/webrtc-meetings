<template>
  <div class="c_home">
    <el-container>
      <el-main class="c_main">
        <div class="c_mainbox">
          <div class="c_mainleft">
            <div
              style="
                height: 180px;
                text-align: left;
                margin-left: 20px;
                font-size: 20px;
              "
            >
              <pre>
欢迎使用这个会议系统，
                                                                                   
                —南苑最好用的会议系统</pre
              >
            </div>
            <div v-if="logined">
              <el-row class="c_main_row">
                <el-button type="primary" @click="startMeeting"
                  >发起会议</el-button
                >
              </el-row>
              <el-row class="c_main_row">
                <el-button type="primary" @click="historyMeeting"
                  >会议记录</el-button
                >
              </el-row>
              <el-row class="c_main_row">
                <input
                  placeholder="请输入会议房间号"
                  v-model="roomCode"
                  class="c_main_room"
                  maxlength="9"
                />
                <div class="c_main_join" @click="joinMeeting">加入会议</div>
              </el-row>
            </div>
            <el-form
              :model="login"
              :rules="loginRules"
              ref="login"
              label-width="0px"
              style="width: 360px; margin: 0 auto"
              v-else
            >
              <el-row class="c_main_row">
                <el-form-item prop="username">
                  <el-input
                    v-model="login.username"
                    placeholder="请输入用户名"
                    class="c_login_input"
                  ></el-input>
                </el-form-item>
              </el-row>
              <el-row class="c_main_row">
                <el-form-item prop="password">
                  <el-input
                    v-model="login.password"
                    placeholder="请输入密码"
                    class="c_login_input"
                  ></el-input>
                </el-form-item>
              </el-row>
              <el-row class="c_main_row">
                <el-button
                  type="primary"
                  @click="userLogin('login')"
                  style="width: 25%"
                  >登录</el-button
                >
              </el-row>
            </el-form>
          </div>
          <div class="c_mainright">
            <el-row>
              <video
                id="localvideo"
                class="c_video"
                autoplay
                muted
                playsinline
              ></video>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form
                  :inline="true"
                  :model="formInline"
                  :rules="rules"
                  ref="formInline"
                  class="c_video_settings"
                >
                  <el-form-item label="摄像头" prop="videoId">
                    <el-select
                      v-model="formInline.videoId"
                      placeholder="摄像头"
                    >
                      <el-option
                        v-for="(item, index) in localDevice.videoIn"
                        :key="index"
                        :label="item.label"
                        :value="item.id"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="麦克风" prop="audioInId">
                    <el-select
                      v-model="formInline.audioInId"
                      placeholder="麦克风"
                    >
                      <el-option
                        v-for="(item, index) in localDevice.audioIn"
                        :key="index"
                        :label="item.label"
                        :value="item.id"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="videochoose('formInline')"
                      >选择</el-button
                    >
                  </el-form-item>
                </el-form>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
function handleError(error) {
  // alert("摄像头无法正常使用，请检查是否占用或缺失")
  console.error(
    "navigator.MediaDevices.getUserMedia error: ",
    error.message,
    error.name
  );
}
var localDevice = null;
function initInnerLocalDevice() {
  const that = this;
  localDevice = {
    audioIn: [],
    videoIn: [],
  };
  let constraints = { video: true, audio: true };
  if (!navigator.mediaDevices || !navigator.mediaDevices.enumerateDevices) {
    console.log("浏览器不支持获取媒体设备");
    return;
  }
  navigator.mediaDevices
    .getUserMedia(constraints)
    .then(function (stream) {
      stream.getTracks().forEach((trick) => {
        trick.stop();
      });

      // List cameras and microphones.
      navigator.mediaDevices
        .enumerateDevices()
        .then(function (devices) {
          devices.forEach(function (device) {
            let obj = {
              id: device.deviceId,
              kind: device.kind,
              label: device.label,
            };
            if (device.kind === "audioinput") {
              if (
                localDevice.audioIn.filter((e) => e.id === device.deviceId)
                  .length === 0
              ) {
                localDevice.audioIn.push(obj);
              }
            } else if (device.kind === "videoinput") {
              if (
                localDevice.videoIn.filter((e) => e.id === device.deviceId)
                  .length === 0
              ) {
                localDevice.videoIn.push(obj);
              }
            }
          });
        })
        .catch(handleError);
    })
    .then(() => {
      console.log(1231)
    }).catch(handleError);
}

export default {
  name: "HomePage",
  data() {
    return {
      logined: false,
      roomCode: "",
      localDevice: {
        audioIn: [],
        videoIn: [],
        audioOut: [],
      },
      formInline: {
        videoId: undefined,
        audioInId: undefined,
      },
      login: {
        username: "",
        password: "",
      },
      loginRules: {
        username: [
          { required: true, message: "用户名不能为空", trigger: "blur" },
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
      },
      rules: {
        videoId: [
          { required: true, message: "请选择摄像头", trigger: "change" },
        ],
        audioInId: [
          { required: true, message: "请选择麦克风", trigger: "change" },
        ],
      },
    };
  },

  created() {
    initInnerLocalDevice();
    this.localDevice = localDevice;
  },
  mounted() {
    this.logined = false;
    if (window.sessionStorage.getItem("token") != undefined) {
      this.$axios
        .post(this.$meetingServerURL + "currentUser")
        .then(async (res) => {
          if (res.code == 200) {
            this.logined = true;
          }else{
            this.$message.error("请先登录");
          }
        })
        .catch((err) => {
          console.error(err);
        });
    }else{
      this.$message.error("请先登录");
    }
  },
  methods: {
    async videochoose(formName) {
      let newStream = await this.getTargetDeviceMedia(
        this.formInline.videoId,
        this.formInline.audioInId
      );
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let videoDom = document.getElementById("localvideo");
          let stream = videoDom.srcObject;
          if (stream) {
            stream.getAudioTracks().forEach((e) => {
              stream.removeTrack(e);
            });
            stream.getVideoTracks().forEach((e) => {
              stream.removeTrack(e);
            });
          }
          videoDom.srcObject = newStream;
          videoDom.muted = true;
        } else {
          return false;
        }
      });
    },
    /**
     * 获取设备 stream
     * @param constraints
     * @returns {Promise<MediaStream>}
     */ async getLocalUserMedia(constraints) {
      return await navigator.mediaDevices.getUserMedia(constraints);
    },
    /**
     * 获取指定媒体设备id对应的媒体流
     * @author suke
     * @param videoId
     * @param audioId
     * @returns {Promise<void>}
     */
    async getTargetDeviceMedia(videoId, audioId) {
      const constraints = {
        audio: { deviceId: audioId ? { exact: audioId } : undefined },
        video: {
          deviceId: videoId ? { exact: videoId } : undefined,
        },
      };
      if (window.stream) {
        window.stream.getTracks().forEach((track) => {
          track.stop();
        });
      }
      //被调用方法前面有，此处不再重复
      return await this.getLocalUserMedia(constraints).catch(handleError);
    },
    async startMeeting() {
      if (!this.formInline.videoId) {
        this.$message.error("请选择摄像头");
        return;
      }
      if (!this.formInline.audioInId) {
        this.$message.error("请选择麦克风");
        return;
      }
      let rcode = undefined;
      await this.$axios
        .post(this.$meetingServerURL + "room/createRoom")
        .then(async (res) => {
          if (res.code == 200) {
            rcode = res.result;
          } else {
            this.$message.error(res.msg);
          }
        })
        .catch((err) => {
          this.$message.error(err);
        });
      if (rcode) {
        this.$router.push({
          path: "/meeting",
          query: {
            videoId: this.formInline.videoId,
            audioInId: this.formInline.audioInId,
            roomCode: rcode,
          },
        });
      }
    },
    async historyMeeting() {
      this.$router.push("/history");
    },
    async joinMeeting() {
      console.log(this.roomCode);
      if (this.roomCode) {
        document
          .querySelector(".c_main_room")
          .classList.remove("roomcode-error");
      } else {
        document.querySelector(".c_main_room").classList.add("roomcode-error");
        return;
      }
      await this.$axios
        .get(
          this.$meetingServerURL + "room/queryRoom?roomCode=" + this.roomCode
        )
        .then(async (res) => {
          if (res.code == 200) {
            if (res.result) {
              if (res.result.endTime) {
                this.$message.error("会议已结束");
              }else{
                this.$router.push({
                path: "/meeting",
                query: {
                  videoId: this.formInline.videoId,
                  audioInId: this.formInline.audioInId,
                  roomCode: this.roomCode,
                },
              });
              }
            } else {
              this.$message.error("会议室不存在");
              document.querySelector(".c_main_room").focus();
            }
          } else {
            this.$message.error(res.msg);
          }
        })
        .catch((err) => {
          this.$message.error(err);
        });
    },
    async userLogin(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$axios
            .post(
              this.$meetingServerURL +
                "login?username=" +
                this.login.username +
                "&password=" +
                this.login.password
            )
            .then(async (res) => {
              if (res.code == 200) {
                this.logined = true;
                window.sessionStorage.setItem("token", res.result.token);
                window.sessionStorage.setItem("username", res.result.username);
                window.sessionStorage.setItem("logined",this.logined)
                this.$router.go(0)
              } else {
                this.$message.error(res.msg);
              }
            })
            .catch((err) => {
              this.$message.error(err);
            });
        } else {
          this.logined = false;
          return false;
        }
      });
    },
  },
};
</script>

<style scoped>
.c_header {
  background-color: dodgerblue;
  text-align: center;
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