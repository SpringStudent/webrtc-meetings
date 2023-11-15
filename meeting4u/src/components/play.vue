<template>
  <div class="preview_container">
    <div style="background: black">
      <div class="video_container">
        <video
          v-show="client.view"
          autoplay
          muted
          :srcObject.prop="client.localStream"
          style="height: 100%; width: 100%"
        />
      </div>
    </div>
    <div class="console_container">
      <span style="max-width: 130px;">        
          <span>{{ client.username }}</span>
          <a v-show="!client.fullScreen" @click="$emit('fullEvent',client.username)"><font-awesome-icon :icon="['fas', 'expand']" style="padding-left:5px" /></a>
          <a v-show="client.fullScreen" @click="$emit('fullEvent',client.username)"><font-awesome-icon :icon="['fas', 'compress']" style="padding-left:5px"/></a>
      </span>
    </div>
  </div>
</template>
        
<script>
export default {
  name: "Play",
 
  props: {
    client: {
      type: Object,
      default: null,
    },
    isRoomAdmin: {
      type: Boolean,
      default: false,
    }
  }
};
</script>
  <style scoped>
.preview_container {
  display: inline-block;
  width: 240px;
  height: 200px;
  margin-right: 3px;
}
.video_container {
  height: 160px;
  background: #939394;
  text-align: center;
}
.console_container {
  height: 25px;
  background: white;
  line-height: 15px;
  text-align: center;
  margin-top: 2px;
}
.console_container .svg-icon {
  font-size: 12px;
}

.console_container a:hover{
  cursor: pointer;
}
</style>
        