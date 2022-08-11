package eu.midnightdust.customsplashscreen.f2f;

import xland.mcmodbridge.fa2fomapper.api.AbstractMapperTransformationService;
import xland.mcmodbridge.fa2fomapper.api.MappingContextProvider;

public class SelfTransform extends AbstractMapperTransformationService {
    @Override
    public String mapperName() {
        return "customsplashscreen-teddyxlandlee";
    }

    @Override
    public MappingContextProvider mappingContext() {
        return new CustomSplashScreenCtxProvider();
    }
}
