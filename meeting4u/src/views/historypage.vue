<template>
  <div id="history_container">
    <el-main class="meeting_his">
      <el-table :data="meetingHis" style="width: 100%" height="100%">
        <el-table-column prop="roomCode" label="房间号"> </el-table-column>
        <el-table-column prop="roomName" label="房间名"> </el-table-column>
        <el-table-column
          prop="startTime"
          label="开始时间"
          :formatter="dateFormat"
        >
        </el-table-column>
        <el-table-column
          prop="endTime"
          label="结束时间"
          :formatter="dateFormat"
        >
        </el-table-column>
        <el-table-column prop="createUser" label="发起人"> </el-table-column>
        <el-table-column
          prop="joinUser"
          label="与会人数"
          :formatter="countUser"
        >
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="100">
          <template slot-scope="scope">
            <el-button @click="detail(scope.row)" type="text" size="small">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-drawer title="会议详情" :visible.sync="drawer">
        <el-form label-width="80px" :model="room" style="margin-left: 10%">
          <el-form-item label="发起人:" style="text-align: left">
            <span>{{ room.createUser }}</span>
          </el-form-item>
          <el-form-item label="房间号:" style="text-align: left">
            <span>{{ room.code }}</span>
          </el-form-item>
          <el-form-item label="会议名称:" style="text-align: left">
            <span>{{ room.name }}</span>
          </el-form-item>
          <el-form-item label="开始时间:" style="text-align: left">
            <span>{{ room.startTime }}</span>
          </el-form-item>
          <el-form-item label="结束时间:" style="text-align: left">
            <span>{{ room.endTime }}</span>
          </el-form-item>
          <el-form-item label="与会成员:" style="text-align: left">
            <span>{{ room.joinUser }}</span>
          </el-form-item>
          <el-form-item label="会议白板:" style="text-align: left">
            <div v-if="room.boardData">
              <el-image
                style="width: 100px; height: 60px"
                :src="room.boardData"
                :preview-src-list="room.boardDataList"
              ></el-image>
            </div>
            <div v-else></div>
          </el-form-item>
          <el-form-item label="聊天记录:" style="text-align: left">
            <div class="chat_his" v-html="room.chatHis"></div>
          </el-form-item>
        </el-form>
      </el-drawer>
    </el-main>
    <el-footer class="meeting_page">
      <el-pagination
        @size-change="sizeChange"
        @current-change="pageChange"
        :current-page="currentPage"
        :page-sizes="[10, 15, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      >
      </el-pagination>
    </el-footer>
  </div>
</template>
<script>
export default {
  name: "HistoryPage",
  data() {
    return {
      meetingHis: [],
      currentPage: 1,
      total: 50,
      pageSize: 15,
      drawer: false,
      room: {
        code:"",
        name: "",
        startTime: "",
        endTime: "",
        createUser: "",
        joinUser: undefined,
        boardData: undefined,
        boardDataList:[],
        chatHis: "",
      },
    };
  },
  mounted() {
    this.meetingHistory();
  },
  methods: {
    detail(row) {
      //加载会议信息
      this.$axios
        .get(this.$meetingServerURL + "room/queryRoom?roomCode=" + row.roomCode)
        .then(async (res) => {
          if (res.code == 200) {
            let roomInfo = res.result;
            this.room.name = roomInfo.roomName;
            this.room.startTime = this.transformDate(roomInfo.startTime);
            this.room.endTime = this.transformDate(roomInfo.endTime);
            this.room.createUser = roomInfo.createUser;
            this.room.boardData = roomInfo.boardData;
            this.room.joinUser = roomInfo.joinUser.join(',');
            this.room.boardDataList.push(roomInfo.boardData)
            this.room.code = roomInfo.roomCode;
          } else {
            this.$message.error(res.msg);
          }
        })
        .catch((err) => {
          this.$message.error("err");
        });
        this.room.chatHis = "";
        //加载会议聊天
        this.$axios
        .get(this.$meetingServerURL + "chat/chatInfos?roomCode=" + row.roomCode)
        .then(async (res) => {
          if (res.code == 200) {
              let chatHist = res.result;
              for(let i = 0;i<chatHist.length;i++){
                   if(chatHist[i].type===1){
                    this.room.chatHis += `<div style="text-align:center;">
                          <div>
                              <div><span style="font-size: 0.7rem">${chatHist[i].content}</span></div>
                          </div>
                      </div>`;
                   }else{
                    this.room.chatHis += `<div style="text-align:left;margin-left:12px"><div>
                              <div><span style="color:#4ecca3;font-weight:bold">${chatHist[i].sender}</span><span style="color:#04da8f;"> ${chatHist[i].sendTime}</span></div>
                          </div>
                          <div>
                              ${chatHist[i].content}
                          </div>
                      </div>`;
                   }
              }
          } else {
            this.$message.error(res.msg);
          }
        })
        .catch((err) => {
          this.$message.error("err");
        });  
      //设置抽屉显示
      this.drawer = true;
    },
    dateFormat(row, column) {
      return this.transformDate(row[column.property]);
    },

    countUser(row, column) {
      return row[column.property].length;
    },
    transformDate(time) {
      if (time) {
        let dt = new Date(parseInt(time));
        return (
          dt.getFullYear() +
          "-" +
          (dt.getMonth() + 1 < 10
            ? "0" + (dt.getMonth() + 1)
            : dt.getMonth() + 1) +
          "-" +
          (dt.getDate() < 10 ? "0" + dt.getDate() : dt.getDate()) +
          " " +
          (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) +
          ":" +
          (dt.getMinutes() < 10 ? "0" + dt.getMinutes() : dt.getMinutes()) +
          ":" +
          (dt.getSeconds() < 10 ? "0" + dt.getSeconds() : dt.getSeconds())
        );
      } else {
        return "";
      }
    },

    pageChange(page) {
      this.currentPage = page;
      this.meetingHistory();
    },
    sizeChange(size) {
      this.pageSize = size;
      this.meetingHistory();
    },

    async meetingHistory() {
      let data = { currentPage: this.currentPage, pageSize: this.pageSize };
      await this.$axios
        .post(this.$meetingServerURL + "room/pageRoom", data)
        .then(async (res) => {
          this.meetingHis = res.result.list;
          this.total = res.result.total;
        })
        .catch((err) => {
          this.$message.error(err);
        });
    },
  },
};
</script>
<style scoped>
.meeting_his {
  /* background-color: #f4f7fa; */
  color: #333;
  height: 82vh;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.meeting_page {
  color: #333;
  margin-top: 12px;
  text-align: center;
}
.chat_his {
  background-color: #f4f7fa;
  height: 320px;
  width: 80%;
  padding: 10px;
  margin-top: 10px;
  padding-top: 10px;
  overflow-y: scroll;
  line-height: 25px;
}
</style>