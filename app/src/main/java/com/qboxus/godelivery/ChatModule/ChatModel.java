package com.qboxus.godelivery.ChatModule;

import java.io.Serializable;


public class ChatModel implements Serializable {

    public String
            receiver_id,
            sender_id,
            chat_id,
            sender_name,
            text,
            pic_url,
            status,
            time,
            timestamp,
            type;
}
