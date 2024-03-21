package io.springstudent.meeting.chat.service;

import cn.hutool.core.bean.BeanUtil;
import com.gysoft.jdbc.bean.Criteria;
import com.gysoft.jdbc.bean.Sort;
import io.springstudent.meeting.chat.bean.ChatInfo;
import io.springstudent.meeting.chat.dao.ChatDao;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhouning
 * @date 2023/04/25 16:15
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatDao chatDao;

    @Override
    public List<ChatInfo> chatInfos(String roomCode) throws Exception {
        return chatDao.queryWithCriteria(new Criteria().where("roomCode",roomCode).orderBy(new Sort("sendTime","ASC")))
                .stream().map(chat -> {
                    ChatInfo chatInfo = BeanUtil.copyProperties(chat,ChatInfo.class);
                    chatInfo.setSendTime(DateFormatUtils.format(chat.getSendTime(),"yyyy-MM-dd HH:mm:ss"));
                    return chatInfo;
                }).collect(Collectors.toList());
    }
}
