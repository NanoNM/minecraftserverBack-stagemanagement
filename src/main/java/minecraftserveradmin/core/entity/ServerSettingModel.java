package minecraftserveradmin.core.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;

/***
 * 不再使用 废弃
 */
@EntityScan
public class ServerSettingModel {
    private String spawn_rotection;
    private String max_tick_time;
    private String query_port;
    private String generator_settings;
    private boolean force_gamemode;
    private boolean allow_nether;
    private boolean enforce_whitelist;
    private String gamemode;
    private boolean broadcast_console_to_ops;
    private boolean enable_query;
    private String player_idle_timeout;
    private String  difficulty;
    private boolean spawn_monsters;
    private boolean broadcast_rcon_to_ops;
    private String op_permission_level;
    private boolean pvp;
    private boolean snooper_enabled;
    private String level_type;
    private boolean hardcore;
    private boolean enable_command_block;
    private String max_players;
    private String network_compression_threshold;
    private String resource_pack_sha1;
    private String max_world_size;
    private String rcon_port;
    private String server_port;
    private String  server_ip;
    private boolean spawn_npcs;
    private boolean allow_flight;
    private String level_name;
    private String view_distance;
    private String resource_pack;
    private boolean spawn_animals;
    private boolean white_list;
    private String rcon_password;
    private boolean generate_structures;
    private boolean online_mode;
    private String max_build_height;
    private String level_seed;
    private boolean prevent_proxy_connections;
    private boolean use_native_transport;
    private String motd;
    private boolean enable_rcon;

    public String getSpawn_rotection() {
        return spawn_rotection;
    }

    public void setSpawn_rotection(String spawn_rotection) {
        this.spawn_rotection = spawn_rotection;
    }

    public String getMax_tick_time() {
        return max_tick_time;
    }

    public void setMax_tick_time(String max_tick_time) {
        this.max_tick_time = max_tick_time;
    }

    public String getQuery_port() {
        return query_port;
    }

    public void setQuery_port(String query_port) {
        this.query_port = query_port;
    }

    public String getGenerator_settings() {
        return generator_settings;
    }

    public void setGenerator_settings(String generator_settings) {
        this.generator_settings = generator_settings;
    }

    public boolean isForce_gamemode() {
        return force_gamemode;
    }

    public void setForce_gamemode(boolean force_gamemode) {
        this.force_gamemode = force_gamemode;
    }

    public boolean isAllow_nether() {
        return allow_nether;
    }

    public void setAllow_nether(boolean allow_nether) {
        this.allow_nether = allow_nether;
    }

    public boolean isEnforce_whitelist() {
        return enforce_whitelist;
    }

    public void setEnforce_whitelist(boolean enforce_whitelist) {
        this.enforce_whitelist = enforce_whitelist;
    }

    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public boolean isBroadcast_console_to_ops() {
        return broadcast_console_to_ops;
    }

    public void setBroadcast_console_to_ops(boolean broadcast_console_to_ops) {
        this.broadcast_console_to_ops = broadcast_console_to_ops;
    }

    public boolean isEnable_query() {
        return enable_query;
    }

    public void setEnable_query(boolean enable_query) {
        this.enable_query = enable_query;
    }

    public String getPlayer_idle_timeout() {
        return player_idle_timeout;
    }

    public void setPlayer_idle_timeout(String player_idle_timeout) {
        this.player_idle_timeout = player_idle_timeout;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isSpawn_monsters() {
        return spawn_monsters;
    }

    public void setSpawn_monsters(boolean spawn_monsters) {
        this.spawn_monsters = spawn_monsters;
    }

    public boolean isBroadcast_rcon_to_ops() {
        return broadcast_rcon_to_ops;
    }

    public void setBroadcast_rcon_to_ops(boolean broadcast_rcon_to_ops) {
        this.broadcast_rcon_to_ops = broadcast_rcon_to_ops;
    }

    public String getOp_permission_level() {
        return op_permission_level;
    }

    public void setOp_permission_level(String op_permission_level) {
        this.op_permission_level = op_permission_level;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isSnooper_enabled() {
        return snooper_enabled;
    }

    public void setSnooper_enabled(boolean snooper_enabled) {
        this.snooper_enabled = snooper_enabled;
    }

    public String getLevel_type() {
        return level_type;
    }

    public void setLevel_type(String level_type) {
        this.level_type = level_type;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    public boolean isEnable_command_block() {
        return enable_command_block;
    }

    public void setEnable_command_block(boolean enable_command_block) {
        this.enable_command_block = enable_command_block;
    }

    public String getMax_players() {
        return max_players;
    }

    public void setMax_players(String max_players) {
        this.max_players = max_players;
    }

    public String getNetwork_compression_threshold() {
        return network_compression_threshold;
    }

    public void setNetwork_compression_threshold(String network_compression_threshold) {
        this.network_compression_threshold = network_compression_threshold;
    }

    public String getResource_pack_sha1() {
        return resource_pack_sha1;
    }

    public void setResource_pack_sha1(String resource_pack_sha1) {
        this.resource_pack_sha1 = resource_pack_sha1;
    }

    public String getMax_world_size() {
        return max_world_size;
    }

    public void setMax_world_size(String max_world_size) {
        this.max_world_size = max_world_size;
    }

    public String getRcon_port() {
        return rcon_port;
    }

    public void setRcon_port(String rcon_port) {
        this.rcon_port = rcon_port;
    }

    public String getServer_port() {
        return server_port;
    }

    public void setServer_port(String server_port) {
        this.server_port = server_port;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }

    public boolean isSpawn_npcs() {
        return spawn_npcs;
    }

    public void setSpawn_npcs(boolean spawn_npcs) {
        this.spawn_npcs = spawn_npcs;
    }

    public boolean isAllow_flight() {
        return allow_flight;
    }

    public void setAllow_flight(boolean allow_flight) {
        this.allow_flight = allow_flight;
    }

    public String getLevel_name() {
        return level_name;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }

    public String getView_distance() {
        return view_distance;
    }

    public void setView_distance(String view_distance) {
        this.view_distance = view_distance;
    }

    public String getResource_pack() {
        return resource_pack;
    }

    public void setResource_pack(String resource_pack) {
        this.resource_pack = resource_pack;
    }

    public boolean isSpawn_animals() {
        return spawn_animals;
    }

    public void setSpawn_animals(boolean spawn_animals) {
        this.spawn_animals = spawn_animals;
    }

    public boolean isWhite_list() {
        return white_list;
    }

    public void setWhite_list(boolean white_list) {
        this.white_list = white_list;
    }

    public String getRcon_password() {
        return rcon_password;
    }

    public void setRcon_password(String rcon_password) {
        this.rcon_password = rcon_password;
    }

    public boolean isGenerate_structures() {
        return generate_structures;
    }

    public void setGenerate_structures(boolean generate_structures) {
        this.generate_structures = generate_structures;
    }

    public boolean isOnline_mode() {
        return online_mode;
    }

    public void setOnline_mode(boolean online_mode) {
        this.online_mode = online_mode;
    }

    public String getMax_build_height() {
        return max_build_height;
    }

    public void setMax_build_height(String max_build_height) {
        this.max_build_height = max_build_height;
    }

    public String getLevel_seed() {
        return level_seed;
    }

    public void setLevel_seed(String level_seed) {
        this.level_seed = level_seed;
    }

    public boolean isPrevent_proxy_connections() {
        return prevent_proxy_connections;
    }

    public void setPrevent_proxy_connections(boolean prevent_proxy_connections) {
        this.prevent_proxy_connections = prevent_proxy_connections;
    }

    public boolean isUse_native_transport() {
        return use_native_transport;
    }

    public void setUse_native_transport(boolean use_native_transport) {
        this.use_native_transport = use_native_transport;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public boolean isEnable_rcon() {
        return enable_rcon;
    }

    public void setEnable_rcon(boolean enable_rcon) {
        this.enable_rcon = enable_rcon;
    }
}
