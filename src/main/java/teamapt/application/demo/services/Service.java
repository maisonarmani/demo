package teamapt.application.demo.services;

import teamapt.application.demo.domains.Base;

import java.util.HashMap;

public interface Service {
    HashMap<String, Base> getReport();
    Base getBase();
}
