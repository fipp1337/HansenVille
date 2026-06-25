package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.repository.PoolTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PoolTemplateService {

    private final PoolTemplateRepository poolTemplateRepository;

}
