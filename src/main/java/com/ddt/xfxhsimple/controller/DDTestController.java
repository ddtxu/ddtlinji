package com.ddt.xfxhsimple.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.ddt.xfxhsimple.thread.DdtThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.FutureTask;

@RestController
@RequestMapping("/test")
@Slf4j
public class DDTestController {
    @Value("${constants.apiKey}")
    private String apiKey;

    @PostMapping("/question")
    public Map<String, Object> flow(@RequestBody Map<String, Object> tokenMap) throws Exception {
        String asrAddr = "127.0.0.1:9988";
        String ttsAddr = "ws://127.0.0.1:9989/tts";
        Long timestamp = (Long) tokenMap.get("timestamp");
        String method = (String) tokenMap.get("method");
        String callid = (String) tokenMap.get("callid");
        String appid = (String) tokenMap.get("appid");
        Map<String, Object> resultMap = new HashMap<>();
        if ("create".equals(method)) {
            String callSource = (String) tokenMap.get("call_source");
            log.info("callSource ===>{}", callSource);
            String sourceName = (String) tokenMap.get("source_name");
            log.info("sourceName ===>{}", sourceName);
            Map<String, Object> ttsMap = new HashMap<>();
            resultMap.put("action", "cti_play_and_detect_speech");
            String date = LocalDateTime.now().toString();
            resultMap.put("argument", "'1' '64' '0' '0.8' '" + asrAddr + "' '120' '800' '5000' '20000' '' '' '" + appid + "' '1' '" + date + "' 'wav'");
            ttsMap.put("ttsurl", ttsAddr);
            ttsMap.put("ttsvoicename", "x4_lingxiaoxuan_en");
            ttsMap.put("ttsconfig", "");
            ttsMap.put("ttsengine", "");
            ttsMap.put("ttsvolume", 0);
            ttsMap.put("ttsspeechrate", 0);
            ttsMap.put("ttspitchrate", 0);
            resultMap.put("tts", ttsMap);
            resultMap.put("privatedata", "test");
            List<String> list = Arrays.asList("欢迎来到顶顶通对接灵积大模型-通义千问的程序，请向大模型提问吧！");
            resultMap.put("playbacks", list);
            resultMap.put("quickresponse", true);
            resultMap.put("log", "create succeed");
        } else if ("input".equals(method)) {
            String privatedata = (String) tokenMap.get("privatedata");
            log.info("privatedata ===>{}", privatedata);
            String input_type = (String) tokenMap.get("input_type");
            log.info("input_type ===>{}", input_type);
            String input_args = (String) tokenMap.get("input_args");
            log.info("input_args ===>{}", input_args);
            Long input_start_time = (Long) tokenMap.get("input_start_time");
            log.info("input_start_time ===>{}", input_start_time);
            Integer input_duration = (Integer) tokenMap.get("input_duration");
            log.info("input_duration ===>{}", input_duration);
            //机器人没放音 0  在放音有时间
            Integer play_progress = (Integer) tokenMap.get("play_progress");
            log.info("play_progress ===>{}", play_progress);
            if ("complete".equals(input_type)) {
                if (input_args.contains("hangup")) {
                    resultMap.put("action", "hangup");
                    resultMap.put("log", "挂机");
                } else {
                    resultMap.put("action", "cti_play_and_detect_speech");
                    String date = LocalDateTime.now().toString();
                    resultMap.put("argument", "'1' '1' '0' '0.8' '" + asrAddr + "' '120' '800' '5000' '20000' '' '' '" + appid + "' '1' '" + date + "' 'wav'");
                    resultMap.put("privatedata", "test");
                    resultMap.put("playbacks", Collections.singletonList("您可以继续向我提问"));
                    resultMap.put("quickresponse", true);
                    resultMap.put("log", "重新开始放音");
                }
            } else {
                String prefix = StrUtil.sub(input_args, 0, 1);
                String text = StrUtil.subSuf(input_args, 1);
                if ("S".equals(prefix)) {
                    if (!"stop".equals(privatedata)) {
                        if (play_progress > 0) {
                            resultMap.put("commands", Collections.singletonList("uuid_cti_play_and_detect_speech_break_play " + callid));
                            resultMap.put("privatedata", "stop");
                            resultMap.put("log", "停止放音，但是不停止ASR识别。模拟关键词打断");
                        }
                    }
                } else if ("F".equals(prefix)) {
                    if (text.contains("挂断")) {
                        resultMap.put("action", "hangup");
                        resultMap.put("privatedata", "test");
                        resultMap.put("playbacks", Collections.singletonList("谢谢你的测试，再见"));
                        resultMap.put("log", "挂机");
                    }
                    else{
                        if(0<play_progress&&text.length()>3||0==play_progress){
                            String str = callWithMessage(text,apiKey);
                            resultMap.put("action", "cti_play_and_detect_speech");
                            String date = LocalDateTime.now().toString();
                            resultMap.put("argument", "'0' '1' '0' '0.8' '" + asrAddr + "' '120' '800' '5000' '20000' '' '' '" + appid + "' '1' '" + date + "' 'wav'");
                            resultMap.put("privatedata", "test");
                            //回答的问题转语音
                            resultMap.put("playbacks", Collections.singletonList(str));
                            resultMap.put("quickresponse", true);
                            resultMap.put("log", "播放识别结果");
                        }
                    }
                }
                if ("D".equals(prefix)) {
                    resultMap.put("action", "cti_play_and_detect_speech");
                    String date = LocalDateTime.now().toString();
                    resultMap.put("argument", "'1' '1' '0' '0.8' '" + asrAddr + "' '120' '800' '10000' '20000' '' '' '" + appid + "' '1' '" + date + "' 'wav'");
                    resultMap.put("privatedata", "test");
                    resultMap.put("dtmf_terminators", "#");
                    resultMap.put("playbacks", Arrays.asList("刚刚的按键内容是", text, "请继续按键测试吧,并以#号键结束"));
                    resultMap.put("log", "按键识别结果");
                } else {
                    resultMap.put("log", "no processing");
                }
            }
        } else if ("destory".equals(method)) {
            resultMap.put("log", "destory succeed");
        }
        return resultMap;
    }
    public  String callWithMessage( String text,String apiKey) throws NoApiKeyException, ApiException, InputRequiredException {
        Constants.apiKey = apiKey;
        Generation gen = new Generation();
        MessageManager msgManager = new MessageManager(10);
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(text).build();
        msgManager.add(userMsg);
        QwenParam param = QwenParam.builder().model(Generation.Models.QWEN_TURBO).messages(msgManager.get())
                .resultFormat(QwenParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .enableSearch(true)
                .build();
        GenerationResult result = gen.call(param);
        if("".equals(result.getOutput().getChoices().get(0))){
            return "";
        }else {
            GenerationOutput.Choice choice = result.getOutput().getChoices().get(0);
            return choice.getMessage().getContent();
        }
    }


}
