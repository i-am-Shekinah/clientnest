package com.clientnest.business;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.clientnest.business.BusinessDto.AddBusinessDto;
import com.clientnest.business.BusinessDto.UpdateBusinessDto;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessService {

    private final BusinessRepository businessRepository;

    @Transactional
    public BusinessDto createBusiness(AddBusinessDto dto) {
        Business business = Business.builder()
                .businessName(dto.businessName())
                .businessDescription(dto.businessDescription())
                .businessEmail(dto.businessEmail())
                .deleted(false)
                .build();

        Business saved =  businessRepository.save(business);
        return BusinessMapper.INSTANCE.toDto(saved);
    }

    @Transactional
    public BusinessDto updateBusiness(UUID businessId, UpdateBusinessDto dto) {
        final var business = businessRepository.findById(businessId)
                .orElseThrow(() -> new EntityNotFoundException("Business not found"));

        business.setBusinessName(dto.businessName());
        business.setBusinessDescription(dto.businessDescription());
        business.setBusinessEmail(dto.businessEmail());
        business.setUpdatedAt(Instant.now());

        return BusinessMapper.INSTANCE.toDto(businessRepository.save(business));
    }

    public BusinessDto getBusiness(UUID businessId) {
        final var business = businessRepository.findById(businessId)
                .orElseThrow(() -> new EntityNotFoundException("Business not found"));

        return BusinessMapper.INSTANCE.toDto(business);
    }

    public Page<BusinessDto> getAllBusinesses(Pageable pageable) {
        Page<Business> businesses = businessRepository.findAll(pageable);

        return businesses.map(BusinessMapper.INSTANCE::toDto);
    }

    public void softDelete(UUID businessId) {
        final var business = businessRepository.findById(businessId)
                .orElseThrow(() -> new EntityNotFoundException("Business not found"));

        business.setDeleted(true);
    }
}
