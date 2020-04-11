package minecraftserveradmin.core.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import minecraftserveradmin.core.entity.ServerSettingModel;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class FormatServerSettingService {
    ServerSettingModel serverSettingModel = new ServerSettingModel();
    public String getModel() throws IOException {
        //读取properties文件
        Properties pro = new Properties();
        FileInputStream in = new FileInputStream("./server.properties");
        pro.load(in);
        in.close();
        //存储properties文件为map
        Map<String,String> map = new HashMap<String,String>();
        Enumeration<Object> keys = pro.keys();
        while(keys.hasMoreElements()){
            String key = (String)keys.nextElement();
            String value = (String)pro.get(key);
            map.put(key,value);
        }
        //pro 2 json
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
/**
 * 发现新的方法 以下不使用
 */
//        serverSettingModel.setSpawn_rotection(pro.getProperty("spawn-protection"));
//        serverSettingModel.setMax_tick_time(pro.getProperty("max-tick-time"));
//        serverSettingModel.setQuery_port(pro.getProperty("query.port"));
//        serverSettingModel.setGenerator_settings(pro.getProperty("generator-settings"));
//        serverSettingModel.setForce_gamemode(Boolean.parseBoolean(pro.getProperty("force-gamemode")));
//        serverSettingModel.setAllow_nether(Boolean.parseBoolean(pro.getProperty("allow-nether")));
//        serverSettingModel.setEnforce_whitelist(Boolean.parseBoolean(pro.getProperty("enforce-whitelist")));
//        serverSettingModel.setGamemode(pro.getProperty("gamemode"));
//        serverSettingModel.setBroadcast_console_to_ops(Boolean.parseBoolean(pro.getProperty("broadcast-console-to-ops")));
//        serverSettingModel.setEnable_query(Boolean.parseBoolean(pro.getProperty("enable-query")));
//        serverSettingModel.setPlayer_idle_timeout(pro.getProperty("player-idle-timeout"));
//        serverSettingModel.setDifficulty(pro.getProperty("difficulty"));
//        serverSettingModel.setSpawn_monsters(Boolean.parseBoolean(pro.getProperty("spawn-monsters")));
//        serverSettingModel.setBroadcast_rcon_to_ops(Boolean.parseBoolean(pro.getProperty("broadcast-rcon-to-ops")));
//        serverSettingModel.setOp_permission_level(pro.getProperty("op-permission-level"));
//        serverSettingModel.setPvp(Boolean.parseBoolean(pro.getProperty("pvp")));
//        serverSettingModel.setSnooper_enabled(Boolean.parseBoolean(pro.getProperty("snooper-enabled")));
//        serverSettingModel.setLevel_type(pro.getProperty("level-type"));
//        serverSettingModel.setHardcore(Boolean.parseBoolean(pro.getProperty("hardcore")));
//        serverSettingModel.setEnable_command_block(Boolean.parseBoolean(pro.getProperty("enable-command-block")));
//        serverSettingModel.setMax_players(pro.getProperty("max-players"));
//        serverSettingModel.setNetwork_compression_threshold(pro.getProperty("network-compression-threshold"));
//        serverSettingModel.setResource_pack_sha1(pro.getProperty("resource-pack-sha1"));
//        serverSettingModel.setMax_world_size(pro.getProperty("max-world-size"));
//        serverSettingModel.setRcon_port(pro.getProperty("rcon.port"));
//        serverSettingModel.setServer_port(pro.getProperty("server-port"));
//        serverSettingModel.setServer_ip(pro.getProperty("server-ip"));
//        serverSettingModel.setSpawn_npcs(Boolean.parseBoolean(pro.getProperty("spawn-npcs")));
//        serverSettingModel.setAllow_flight(Boolean.parseBoolean(pro.getProperty("allow-flight")));
//        serverSettingModel.setLevel_name(pro.getProperty("level-name"));
//        serverSettingModel.setView_distance(pro.getProperty("view-distance"));
//        serverSettingModel.setResource_pack(pro.getProperty("resource-pack"));
//        serverSettingModel.setSpawn_animals(Boolean.parseBoolean(pro.getProperty("spawn-animals")));
//        serverSettingModel.setWhite_list(Boolean.parseBoolean(pro.getProperty("white-list")));
//        serverSettingModel.setRcon_password(pro.getProperty("rcon.password"));
//        serverSettingModel.setGenerate_structures(Boolean.parseBoolean(pro.getProperty("generate-structures")));
//        serverSettingModel.setOnline_mode(Boolean.parseBoolean(pro.getProperty("online-mode")));
//        serverSettingModel.setMax_build_height(pro.getProperty("max-build-height"));
//        serverSettingModel.setLevel_seed(pro.getProperty("level-seed"));
//        serverSettingModel.setPrevent_proxy_connections(Boolean.parseBoolean(pro.getProperty("prevent-proxy-connections")));
//        serverSettingModel.setUse_native_transport(Boolean.parseBoolean(pro.getProperty("use-native-transport")));
//        serverSettingModel.setMotd(pro.getProperty("motd"));
//        serverSettingModel.setEnable_rcon(Boolean.parseBoolean(pro.getProperty("enable-rcon")));

        return jsonStr;
    }
    public void settingFormat(JSONObject jsonParam) throws IOException {
//        Properties prop = new Properties();

//        prop.setProperty("spawn-protection", jsonParam.getString("spawn_rotection"));
//        prop.setProperty("max-tick-time", jsonParam.getString("max_tick_time"));
//        prop.setProperty("query.port", jsonParam.getString("query_port"));
//        prop.setProperty("generator-settings", jsonParam.getString("generator_settings"));
//        prop.setProperty("force-gamemode", jsonParam.getString("force_gamemode"));
//        prop.setProperty("allow-nether", jsonParam.getString("allow_nether"));
//        prop.setProperty("enforce-whitelist", jsonParam.getString("enforce_whitelist"));
//        prop.setProperty("gamemode", jsonParam.getString("gamemode"));
//        prop.setProperty("broadcast-console-to-ops", jsonParam.getString("broadcast_console_to_ops"));
//        prop.setProperty("enable-query", jsonParam.getString("enable_query"));
//
//        prop.setProperty("player-idle-timeout", jsonParam.getString("player_idle_timeout"));
//        prop.setProperty("difficulty", jsonParam.getString("difficulty"));
//        prop.setProperty("spawn-monsters", jsonParam.getString("spawn_monsters"));
//        prop.setProperty("broadcast-rcon-to-ops", jsonParam.getString("broadcast_rcon_to_ops"));
//        prop.setProperty("op-permission-level", jsonParam.getString("op_permission_level"));
//        prop.setProperty("pvp", jsonParam.getString("pvp"));
//        prop.setProperty("snooper-enabled", jsonParam.getString("snooper_enabled"));
//        prop.setProperty("level-type", jsonParam.getString("level_type"));
//        prop.setProperty("hardcore", jsonParam.getString("hardcore"));
//        prop.setProperty("enable-command-block", jsonParam.getString("enable_command_block"));
//
//        prop.setProperty("max-players", jsonParam.getString("max_players"));
//        prop.setProperty("network-compression-threshold", jsonParam.getString("network_compression_threshold"));
//        prop.setProperty("resource-pack-sha1", jsonParam.getString("resource_pack_sha1"));
//        prop.setProperty("max-world-size", jsonParam.getString("max_world_size"));
//        prop.setProperty("rcon.port", jsonParam.getString("rcon_port"));
//        prop.setProperty("server-port", jsonParam.getString("server_port"));
//        prop.setProperty("server-ip", jsonParam.getString("server_ip"));
//        prop.setProperty("spawn-npcs", jsonParam.getString("spawn_npcs"));
//        prop.setProperty("allow-flight", jsonParam.getString("allow_flight"));
//        prop.setProperty("level-name", jsonParam.getString("level_name"));
//
//        prop.setProperty("view-distance", jsonParam.getString("view_distance"));
//        prop.setProperty("resource-pack", jsonParam.getString("resource_pack"));
//        prop.setProperty("spawn-animals", jsonParam.getString("spawn_animals"));
//        prop.setProperty("white-list", jsonParam.getString("white_list"));
//        prop.setProperty("rcon.password", jsonParam.getString("rcon_password"));
//        prop.setProperty("generate-structures", jsonParam.getString("generate_structures"));
//        prop.setProperty("online-mode", jsonParam.getString("online_mode"));
//        prop.setProperty("max-build-height", jsonParam.getString("max_build_height"));
//        prop.setProperty("level-seed", jsonParam.getString("level_seed"));
//        prop.setProperty("prevent-proxy-connections", jsonParam.getString("prevent_proxy_connections"));
//
//        prop.setProperty("use-native-transport", jsonParam.getString("use_native_transport"));
//        prop.setProperty("motd", jsonParam.getString("motd"));
//        prop.setProperty("enable-rcon", jsonParam.getString("enable_rcon"));

        jsonToProp(jsonParam,"./server.properties");
    }

    public  void jsonToProp(JSONObject jsonObject,String Path) {
        Map<String, Object> map= jsonObject.getInnerMap();
        Properties properties=new Properties();
        for (String key : map.keySet()) {
            properties.setProperty(key, map.get(key).toString());
        }
        try {
            OutputStream fos = new FileOutputStream(Path);
            System.err.println(properties.toString());
            try {
                properties.store(fos, properties.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
