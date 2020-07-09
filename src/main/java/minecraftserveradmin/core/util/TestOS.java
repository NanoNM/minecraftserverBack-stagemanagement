package minecraftserveradmin.core.util;

import minecraftserveradmin.core.services.GetServerInfoService;

public class TestOS {
    GetServerInfoService getServerInfoService = new GetServerInfoService();
    public boolean testSystem(){
        return "Windows".equals(getServerInfoService.getSystem());
    }
}
