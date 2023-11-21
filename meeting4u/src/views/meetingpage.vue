<template>
  <div id="meeting_container">
    <el-container>
      <div class="meeting_title" style="width: 100%; margin-bottom: 15px">
        <label>会议名称：</label>
        <el-input v-model="roomName" style="width: 200px" maxlength="20">
          <el-button
            slot="append"
            size="mini"
            icon="el-icon-edit-outline"
            @click="edit"
          />
        </el-input>
        <label style="padding-left: 30px">房间号：</label>
        <el-input v-model="roomCode" style="width: 200px" disabled>
          <el-button
            slot="append"
            size="mini"
            icon="el-icon-document-copy"
            @click="copy"
          />
        </el-input>
        <label style="padding-left: 30px"></label>
        <el-button @click="leave">离开会议</el-button>
      </div>
      <el-header height="200px" style="text-align: left">
        <template v-for="client in clients">
          <play
            v-if="client !== undefined"
            :key="client.username"
            :client="client"
            :is-room-admin="client.isRoomAdmin"
            @fullEvent="fullScreen"
          />
        </template>
      </el-header>
      <el-container>
        <el-main>
          <div
            style="
              width: 1000px;
              margin: 0 auto;
              margin-top: 20px;
              height: 559px;
              box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
            "
          >
            <video
              v-show="this.fullScreenUsername != ''"
              ref="video_full"
              muted
              autoplay
              style="width: 100%; height: 100%"
            />
            <div class="whiteboard-cont" v-show="this.fullScreenUsername == ''">
              <canvas
                id="whiteboard"
                ref="whiteboard"
                @mousedown="drawStart"
                @mousemove="drawIn"
                @mouseup="drawEnd"
                width="1000"
                height="559"
              ></canvas>
              <div class="colors-cont">
                <div class="black" @click="setColor('black')"></div>
                <div class="red" @click="setColor('#e74c3c')"></div>
                <div class="yellow" @click="setColor('#f1c40f')"></div>
                <div class="green" @click="setColor('#badc58')"></div>
                <div class="blue" @click="setColor('#3498db')"></div>
                <div class="orange" @click="setColor('#e67e22')"></div>
                <div class="purple" @click="setColor('#9b59b6')"></div>
                <div class="pink" @click="setColor('#fd79a8')"></div>
                <div class="brown" @click="setColor('#834c32')"></div>
                <div class="grey" @click="setColor('gray')"></div>
                <div class="txt" @click="setTxt()">
                  <font-awesome-icon :icon="['fas', 'font']" size="xl" />
                </div>
                <div class="square" @click="setSqr()">
                  <font-awesome-icon :icon="['far', 'square']" size="xl" />
                </div>
                <div class="clearboard" @click="clearBoard()">
                  <font-awesome-icon :icon="['fas', 'trash-can']" size="xl" />
                </div>
                <div class="eraser" @click="setEraser()">
                  <font-awesome-icon :icon="['fas', 'eraser']" size="xl" />
                </div>
              </div>
              <div class="txtinput"></div>
            </div>
          </div>
        </el-main>
        <el-aside width="580px">
          <el-form :model="chatForm" :rules="rules" ref="chatForm">
            <div class="chat_container" v-html="chatList"></div>
            <el-form-item prop="sendMsg">
              <div>
                <el-input
                  v-model="chatForm.sendMsg"
                  placeholder="请输入内容"
                  maxlength="64"
                  class="input-with-select"
                  ref="chatInput"
                >
                  <el-button
                    slot="append"
                    size="mini"
                    icon="el-icon-s-promotion"
                    @click="send('chatForm')"
                  />
                </el-input>
              </div>
            </el-form-item>
          </el-form>
        </el-aside>
      </el-container>
    </el-container>
  </div>
</template>
<script>
import Play from "../components/play.vue";
import io from "socket.io-client";

navigator.getUserMedia =
  navigator.getUserMedia ||
  navigator.webkitGetUserMedia ||
  navigator.mozGetUserMedia;
var PeerConnection =
  window.RTCPeerConnection ||
  window.mozRTCPeerConnection ||
  window.webkitRTCPeerConnection;
export default {
  components: { Play },
  name: "MeetingPage",
  data() {
    return {
      x: 0,
      y: 0,
      color: "black",
      drawSize: 3,
      colorRemote: "black",
      txtPos: [],
      drawType: "line",
      isDrawing: false,
      inputTxt: "",
      drawsizeRemote: 3,
      editingIndex: -1,
      ctx: undefined,
      selectedText: null,
      currentRect: null,
      roomCode: "",
      roomName: "",
      textData: [],
      chatForm: {
        sendMsg: "",
      },
      receiveMsg: "",
      socket: undefined,
      chatList: "",
      peerConnectionMap: new Map(),
      fullScreenUsername: "",
      clients: [
        {
          username: window.sessionStorage.getItem("username"),
          localStream: undefined,
          peerConnection: undefined,
          muted: false,
          view: true,
          chat: true,
          isRoomAdmin: false,
          isSelf: true,
          fullScreen: false,
        },
      ],
      rules: {
        sendMsg: [
          { required: true, message: "消息内容不能为空", trigger: "blur" },
          {
            min: 1,
            max: 64,
            message: "长度在 1 到 64 个字符",
            trigger: "blur",
          },
        ],
      },
    };
  },
  beforeDestroy() {
    this.socket.disconnect();
  },
  mounted() {
    //初始化canvas
    this.ctx = this.$refs.whiteboard.getContext("2d");
    const queryParams = this.$route.query;
    this.roomCode = queryParams.roomCode;
    this.$axios
      .get(this.$meetingServerURL + "room/queryRoom?roomCode=" + this.roomCode)
      .then(async (res) => {
        if (res.code == 200) {
          this.roomName = res.result.roomName;
        } else {
          this.$message.error(res.msg);
        }
      })
      .catch((err) => {
        this.$message.error(err);
      });
    const constraints = {
      audio: {
        deviceId: queryParams.audioId
          ? { exact: queryParams.audioId }
          : undefined,
      },
      video: {
        deviceId: queryParams.videoId
          ? { exact: queryParams.videoId }
          : undefined,
        width: 1000,
        height: 559,
      },
    };
    //1.初始化个人摄像头
    navigator.mediaDevices
      .getUserMedia(constraints)
      .then((ls) => {
        this.clients[0].localStream = ls;
        //2.连接到会议室
        this.socket = io.connect(
          this.$meetingWebsocketURL +
            "?roomCode=" +
            this.roomCode +
            "&username=" +
            this.clients[0].username
        );
        this.socket.on("connect", () => {
          this.socket.emit("roomUser");
          this.socket.emit("getChat");
        });
        this.socket.on("joinRoom", (username, msg) => {
          this.socket.emit("getBoard");
          if (this.clients[0].username === username) {
            this.pushSdpSrs();
          } else {
            this.pullSdpSrs(username);
          }
          this.chatList += `<div style="text-align:center;margin-top:5px;margin-bottom:5px">
                          <div>
                              <div><span style="font-size: 0.7rem">${msg}</span></div>
                          </div>
                      </div>`;
        });
        this.socket.on("leaveRoom", (username, msg) => {
          if (this.clients[0].username != username) {
            this.chatList += `<div style="text-align:center;margin-top:5px;margin-bottom:5px">
                          <div>
                              <div><span style="font-size: 0.7rem">${msg}</span></div>
                          </div>
                      </div>`;
          }
          this.clients = this.clients.filter(
            (item) => item.username != username
          );
        });
        this.socket.on("roomUser", (users) => {
          for (let i = 0; i < users.length; i++) {
            if (users[i] != this.clients[0].username) {
              console.log(users[i]);
              this.pullSdpSrs(users[i]);
            }
          }
        });
        this.socket.on("receiveMsg", (username, time, msg) => {
          //自己发送的消息，靠右侧
          if (username === this.clients[0].username) {
            this.chatList += `<div style="text-align:right;margin-right:12px">`;
          } else {
            this.chatList += `<div style="text-align:left;margin-left:12px">`;
          }
          this.chatList += `<div>
                              <div><span style="color:#4ecca3;font-weight:bold">${username}</span><span style="color:#04da8f;"> ${time}</span></div>
                          </div>
                          <div style="margin-top:5px;margin-bottom:5px">
                              ${msg}
                          </div>
                      </div>`;
        });
        this.socket.on("meetingEvent_editName", (roomName, msg) => {
          console.log(msg);
          this.chatList += `<div style="text-align:center;margin-top:5px;margin-bottom:5px">
                          <div>
                              <div><span style="font-size: 0.7rem">${msg}</span></div>
                          </div>
                      </div>`;
          this.roomName = roomName;
        });
        this.socket.on(
          "drawLine",
          (newx, newy, oldx, oldy, color, drawSize) => {
            this.colorRemote = color;
            this.drawsizeRemote = drawSize;
            this.drawRemote(newx, newy, oldx, oldy);
          }
        );
        this.socket.on("drawText", (drawTxt, drawX, drawY) => {
          this.drawText(drawTxt, drawX, drawY);
        });
        this.socket.on("reDrawText", (drawTxt, drawX, drawY, width, height) => {
          this.ctx.clearRect(drawX, drawY, width, height);
          this.drawText(drawTxt, drawX, drawY);
        });
        this.socket.on("drawSqr", (x, y, width, height) => {
          this.drawSquare(x, y, width, height);
        });
        this.socket.on("clearBoard", () => {
          this.ctx.clearRect(0, 0, 1000, 559);
        });
        this.socket.on("getBoard", (boardData) => {
          let img = new Image();
          img.onload = () => {
            this.ctx.drawImage(img, 0, 0);
          };
          img.src = boardData;
        });
        this.socket.on("getChat", (chatData) => {
          chatData.forEach((item) => {
            if (item.type === 1) {
              this.chatList += `<div style="text-align:center;margin-top:5px;margin-bottom:5px">
                          <div>
                              <div><span style="font-size: 0.7rem">${item.content}</span></div>
                          </div>
                      </div>`;
            } else if (item.type === 0) {
              if (item.sender === this.clients[0].username) {
                this.chatList += `<div style="text-align:right;margin-right:12px">`;
              } else {
                this.chatList += `<div style="text-align:left;margin-left:12px">`;
              }
              this.chatList += `<div>
                              <div><span style="color:#4ecca3;font-weight:bold">${item.sender}</span><span style="color:#04da8f;"> ${item.sendTime}</span></div>
                          </div>
                          <div style="margin-top:5px;margin-bottom:5px">
                              ${item.content}
                          </div>
                      </div>`;
            }
          });
        });
      })
      .catch((error) => {
        this.$message.error("获取媒体失败，请检查后重试");
        this.$refs.chatInput.disabled = true;
        this.$refs.whiteboard.classList.add("whiteboard-cont-disable");
      });
  },
  methods: {
    async send(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.socket.emit("sendMsg", this.chatForm.sendMsg);
          this.chatForm.sendMsg = "";
        } else {
          return false;
        }
      });
    },
    async edit() {
      this.$axios
        .post(
          this.$meetingServerURL +
            "room/updateRoomName?roomCode=" +
            this.roomCode +
            "&roomName=" +
            this.roomName
        )
        .then(async (res) => {
          if (res.code === 200) {
            this.$message.success("会议名称修改成功");
            this.socket.emit("meetingEvent", {
              eventName: "editName",
              roomCode: this.roomCode,
              roomName: this.roomName,
              username: this.clients[0].username,
            });
          } else {
            this.$message.error("会议名称修改失败" + res.msg);
          }
        })
        .catch((err) => {
          this.$message.error("会议名称修改失败");
        });
    },
    async copy() {
      /* 复制内容到文本域 */
      navigator.clipboard.writeText(this.roomCode);
      this.$message.success("房间号复制成功");
    },
    async pushSdpSrs() {
      this.clients[0].peerConnection = await new PeerConnection(null);
      let streamId = this.clients[0].username;
      let pc = this.clients[0].peerConnection;
      pc.addTransceiver("audio", { direction: "sendonly" });
      pc.addTransceiver("video", { direction: "sendonly" });
      this.clients[0].localStream.getTracks().forEach(function (track) {
        pc.addTrack(track);
      });
      let offer = await pc.createOffer();
      await pc.setLocalDescription(offer);
      let data = {
        api: this.$srsServerAPIURL + "rtc/v1/publish/",
        streamurl: this.$srsServerRTCURL + streamId,
        sdp: offer.sdp,
      };
      this.$axios
        .post(this.$srsServerAPIURL + "rtc/v1/publish/", data)
        .then(async (res) => {
          console.log(res);
          if (res.code === 0) {
            await pc.setRemoteDescription(
              new RTCSessionDescription({ type: "answer", sdp: res.sdp })
            );
            this.$message.success("媒体流发布成功");
          } else {
            this.$message.error("推流失败请重试");
          }
        })
        .catch((err) => {
          this.$message.error("推流异常，请检查流媒体服务器");
        });
    },
    async leave() {
      if (this.socket && this.socket.connected) {
        await this.socket.disconnect();
      }
      this.$router.push("/home");
    },
    async pullSdpSrs(user) {
      let streamId = user;

      let client = {
        username: streamId,
        localStream: undefined,
        peerConnection: undefined,
        muted: false,
        view: true,
        chat: true,
        isRoomAdmin: false,
        isSelf: false,
        fullScreen: false,
      };
      let pc = this.peerConnectionMap.get(streamId);
      if (pc) {
        pc.close();
      } else {
        this.peerConnectionMap.set(streamId, pc);
      }
      pc = await new PeerConnection(null);
      pc.addTransceiver("audio", { direction: "recvonly" });
      pc.addTransceiver("video", { direction: "recvonly" });
      pc.ontrack = function (e) {
        let stream = new MediaStream();
        stream.addTrack(e.track);
        client.localStream = stream;
      };
      let offer = await pc.createOffer();
      await pc.setLocalDescription(offer);
      let data = {
        api: this.$srsServerAPIURL + "rtc/v1/play/",
        streamurl: this.$srsServerRTCURL + streamId,
        sdp: offer.sdp,
      };
      this.$axios
        .post(this.$srsServerAPIURL + "rtc/v1/play/", data)
        .then(async (res) => {
          if (res.code === 0) {
            await pc.setRemoteDescription(
              new RTCSessionDescription({ type: "answer", sdp: res.sdp })
            );
          }
        })
        .catch((err) => {
          console.error("SRS 拉流异常", err);
        });
      client.peerConnection = pc;
      client.username = streamId;
      this.clients.push(client);
    },
    fullScreen(username) {
      for (let i = 0; i < this.clients.length; i++) {
        if (this.clients[i].username == username) {
          if (!this.clients[i].fullScreen) {
            this.fullScreenUsername = username;
            this.$refs.video_full.srcObject = this.clients[i].localStream;
            this.clients[i].fullScreen = true;
          } else {
            this.clients[i].fullScreen = false;
            if (this.fullScreenUsername == username) {
              this.$refs.video_full.srcObject = null;
              this.fullScreenUsername = "";
            }
          }
        }
      }
    },
    findClickedText(x, y) {
      return this.textData.find((data) => {
        const textWidth = this.ctx.measureText(data.txt).width;
        const textHeight = 14;
        return (
          x >= data.x &&
          x <= data.x + textWidth &&
          y >= data.y - textHeight &&
          y <= data.y
        );
      });
    },
    editSelectedText(e) {
      if (this.selectedText) {
        let sx = this.selectedText.x;
        let sy = this.selectedText.y;
        let sid = this.selectedText.id;
        let stxt = this.selectedText.txt;
        let textInput = document.createElement("input");
        textInput.type = "text";
        textInput.value = this.selectedText.txt;
        textInput.style.position = "absolute";
        textInput.style.left = sx + "px";
        textInput.style.top = sy + "px";
        document.querySelector(".txtinput").appendChild(textInput);
        textInput.focus();
        textInput.addEventListener("blur", () => {
          let newText = textInput.value;
          if (this.selectedText) {
            this.selectedText.txt = newText;
            this.selectedText = null;
          }
          let width = this.ctx.measureText(stxt).width;
          this.ctx.clearRect(sx, sy, width, 14);
          this.drawText(newText, sx, sy);
          this.socket.emit("reDrawText", sid, newText, sx, sy, width, 14);
          document.querySelector(".txtinput").removeChild(textInput);
        });
      }
    },
    setTxt() {
      this.ctx.textBaseline = "top";
      this.drawType = "text";
    },
    setSqr() {
      this.drawType = "square";
    },
    setColor(newcolor) {
      this.color = newcolor;
      this.drawsize = 3;
      this.drawType = "line";
    },
    setEraser() {
      this.color = "white";
      this.drawsize = 20;
      this.drawType = "line";
    },
    clearBoard() {
      if (window.confirm("确定要清空画板吗")) {
        this.ctx.clearRect(0, 0, 1000, 559);
        this.socket.emit("clearBoard");
      } else {
        return;
      }
    },
    drawStart(e) {
      if (this.drawType === "text") {
        e.preventDefault();
        this.$axios
          .get(
            this.$meetingServerURL +
              "room/listRoomBoardTxt?roomCode=" +
              this.roomCode
          )
          .then(async (res) => {
            if (res.code == 200) {
              this.textData = res.result;
            } else {
              this.$message.error(res.msg);
            }
          })
          .catch((err) => {
            this.$message.error(err);
          });
        let rect = this.$refs.whiteboard.getBoundingClientRect();
        let x = e.clientX - rect.left;
        let y = e.clientY - rect.top;
        let clickedText = this.findClickedText(x, y);
        if (clickedText) {
          this.selectedText = clickedText;
          this.editSelectedText(e);
        } else {
          let position = { x: e.offsetX, y: e.offsetY };
          let textInput = document.createElement("input");
          let id = Date.now() + Math.random().toString(36).substr(2, 9);
          textInput.type = "text";
          textInput.style.border = "none";
          textInput.style.position = "absolute";
          textInput.style.left = e.offsetX + "px";
          textInput.style.top = e.offsetY + "px";
          document.querySelector(".txtinput").appendChild(textInput);
          textInput.focus();
          textInput.addEventListener("blur", () => {
            let txt = textInput.value;
            document.querySelector(".txtinput").removeChild(textInput);
            if (txt) {
              if (this.selectedText) {
                this.selectedText.txt = txt;
                this.selectedText = null;
              } else {
                this.drawText(txt, position.x, position.y);
                this.socket.emit("drawText", id, txt, position.x, position.y);
              }
            }
          });
        }
      } else if (this.drawType === "line") {
        this.isDrawing = true;
        this.x = e.offsetX;
        this.y = e.offsetY;
      } else if (this.drawType === "square") {
        this.isDrawing = true;
        this.x = e.offsetX;
        this.y = e.offsetY;
        this.currentRect = document.createElement("div");
        this.currentRect.classList.add("rectangle");
        document.querySelector(".txtinput").appendChild(this.currentRect);
        this.drawIn(e);
      }
    },

    drawIn(e) {
      if (this.isDrawing) {
        if (this.drawType === "line") {
          this.drawLine(e.offsetX, e.offsetY, this.x, this.y);
          this.socket.emit(
            "drawLine",
            e.offsetX,
            e.offsetY,
            this.x,
            this.y,
            this.color,
            this.drawsize
          );
          this.x = e.offsetX;
          this.y = e.offsetY;
        } else if (this.drawType === "square") {
          let width = e.offsetX - this.$refs.whiteboard.offsetLeft - this.x;
          let height = e.offsetY - this.$refs.whiteboard.offsetTop - this.y;
          this.currentRect.style.width = `${width}px`;
          this.currentRect.style.height = `${height}px`;
          this.currentRect.style.left = `${this.x}px`;
          this.currentRect.style.top = `${this.y}px`;
        }
      }
    },
    drawEnd(e) {
      if (this.drawType === "line") {
        this.isDrawing = false;
      }
      if (this.drawType === "square") {
        this.isDrawing = false;
        this.drawSquare(
          this.x,
          this.y,
          this.currentRect.offsetWidth,
          this.currentRect.offsetHeight
        );
        this.socket.emit(
          "drawSqr",
          this.x,
          this.y,
          this.currentRect.offsetWidth,
          this.currentRect.offsetHeight
        );
        document.querySelector(".txtinput").removeChild(this.currentRect);
      }
    },
    drawLine(newx, newy, oldx, oldy) {
      this.ctx.strokeStyle = this.color;
      this.ctx.lineWidth = this.drawsize;
      this.ctx.beginPath();
      this.ctx.moveTo(oldx, oldy);
      this.ctx.lineTo(newx, newy);
      this.ctx.stroke();
      this.ctx.closePath();
      this.socket.emit("storeBoard", this.$refs.whiteboard.toDataURL());
    },
    drawRemote(newx, newy, oldx, oldy) {
      this.ctx.strokeStyle = this.colorRemote;
      this.ctx.lineWidth = this.drawsizeRemote;
      this.ctx.beginPath();
      this.ctx.moveTo(oldx, oldy);
      this.ctx.lineTo(newx, newy);
      this.ctx.stroke();
      this.ctx.closePath();
    },
    drawText(text, x, y) {
      this.ctx.font = "14px Arial";
      this.ctx.fillStyle = "#000";
      this.ctx.fillText(text, x, y);
      this.socket.emit("storeBoard", this.$refs.whiteboard.toDataURL());
    },
    drawSquare(x, y, width, height) {
      this.ctx.strokeStyle = "#000";
      this.ctx.lineWidth = 2;
      this.ctx.strokeRect(x, y, width, height);
    },
  },
};
</script>
<style scoped>
.meeting_title {
  float: left;
}
.el-header {
  background-color: #f4f7fa;
  padding: 0;
  margin: 0;
  overflow-x: scroll;
  overflow-y: hidden;
  white-space: nowrap;
}
.el-aside {
  background-color: #f4f7fa;
  margin: 0;
  padding-top: 8px;
  padding-left: 10px;
  padding-right: 10px;
  padding-bottom: 20px;
}
.el-main {
  background-color: #f4f7fa;
  padding: 0;
}
.el-container {
  height: calc(100vh - 84px);
}
.chat_container {
  background-color: #ffffff;
  height: 480px;
  padding: 10px;
  margin-top: 20px;
  padding-top: 10px;
  overflow-y: scroll;
}

.whiteboard-cont {
  position: relative;
}
.whiteboard-cont-disable {
  cursor: not-allowed;
  pointer-events: none;
  background-color: #f4f7fa !important;
}
.whiteboard-cont #whiteboard {
  background-color: white;
  border-radius: 10px;
  position: absolute;
  left: 0;
  top: 0;
}
.whiteboard-cont .colors-cont {
  display: flex;
  flex-direction: column;
  position: absolute;
  right: 60px;
  top: 15px;
}
.whiteboard-cont .colors-cont .black {
  height: 30px;
  width: 30px;
  border-radius: 50px;
  background-color: black;
  margin-top: 3px;
}
.whiteboard-cont .colors-cont .black:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .red {
  height: 30px;
  width: 30px;
  border-radius: 50px;
  background-color: #e74c3c;
  margin-top: 10px;
}
.whiteboard-cont .colors-cont .red:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .yellow {
  height: 30px;
  width: 30px;
  border-radius: 50px;
  background-color: #f1c40f;
  margin-top: 10px;
}
.whiteboard-cont .colors-cont .yellow:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .green {
  height: 30px;
  width: 30px;
  border-radius: 50px;
  background-color: #badc58;
  margin-top: 10px;
}
.whiteboard-cont .colors-cont .green:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .orange {
  height: 30px;
  width: 30px;
  border-radius: 50px;
  background-color: #e67e22;
  margin-top: 10px;
}
.whiteboard-cont .colors-cont .orange:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .purple {
  height: 30px;
  width: 30px;
  border-radius: 50px;
  background-color: #9b59b6;
  margin-top: 10px;
}
.whiteboard-cont .colors-cont .purple:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .blue {
  height: 30px;
  width: 30px;
  border-radius: 50px;
  background-color: #3498db;
  margin-top: 10px;
}
.whiteboard-cont .colors-cont .blue:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .pink {
  height: 30px;
  width: 30px;
  border-radius: 50px;
  background-color: #fd79a8;
  margin-top: 10px;
}
.whiteboard-cont .colors-cont .pink:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .brown {
  height: 30px;
  width: 30px;
  border-radius: 50px;
  background-color: #834c32;
  margin-top: 10px;
}
.whiteboard-cont .colors-cont .brown:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .grey {
  height: 30px;
  width: 30px;
  border-radius: 50px;
  background-color: gray;
  margin-top: 10px;
}
.whiteboard-cont .colors-cont .grey:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .eraser {
  height: 20px;
  width: 20px;
  margin-top: 10px;
  color: #2b2b2b;
}
.whiteboard-cont .colors-cont .txt {
  height: 20px;
  width: 20px;
  margin-top: 10px;
  color: #2b2b2b;
}
.whiteboard-cont .colors-cont .txt:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .square {
  height: 20px;
  width: 20px;
  margin-top: 10px;
  color: #2b2b2b;
}
.whiteboard-cont .colors-cont .square:hover {
  cursor: pointer;
}

.whiteboard-cont .colors-cont .eraser:hover {
  cursor: pointer;
}
.whiteboard-cont .colors-cont .clearboard {
  height: 20px;
  width: 20px;
  margin-top: 10px;
  color: #2b2b2b;
}
.whiteboard-cont .colors-cont .clearboard:hover {
  cursor: pointer;
}
.txtinput {
  position: relative;
  z-index: 100;
  margin: 0px;
  padding: 0px;
  background-color: transparent;
  border-radius: 0px;
  border: none;
}
</style>
<style >
.rectangle {
  position: absolute;
  border: 2px solid #000;
  pointer-events: none;
}
</style>