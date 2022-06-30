package dev.randolph.service;

import dev.randolph.model.DTO.MetaDTO;

public class MetaService {
    
    public MetaDTO getMetaData() {
        return MetaDTO.getMetaDTO();
    }

}
