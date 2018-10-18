package teamapt.application.demo.services;


import teamapt.application.demo.domains.Base;

import java.util.HashMap;
@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    @Override
    public HashMap<String, Base> getReport() {
        return null;
    }

    @Override
    public Base getBase() {
        return new Base();
    }
}
