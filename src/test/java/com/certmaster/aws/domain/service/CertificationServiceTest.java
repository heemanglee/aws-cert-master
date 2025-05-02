package com.certmaster.aws.domain.service;

import com.certmaster.aws.application.service.CertificationServiceImpl;
import com.certmaster.aws.domain.entity.Certification;
import com.certmaster.aws.domain.repository.CertificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CertificationServiceTest {

    @Mock
    private CertificationRepository certificationRepository;

    @InjectMocks
    private CertificationServiceImpl certificationService;

    private Certification foundationalCert;
    private Certification associateCert;

    @BeforeEach
    void setUp() {
        // 테스트용 자격증 객체 생성
        foundationalCert = new Certification(
            "CLF-C01", 
            "AWS Certified Cloud Practitioner", 
            "기초 수준 자격증", 
            100.0, 
            "영어, 한국어",
            "Foundational"
        );
        
        associateCert = new Certification(
            "SAA-C03", 
            "AWS Certified Solutions Architect - Associate", 
            "어소시에이트 수준 자격증", 
            150.0, 
            "영어, 한국어", 
            "Associate"
        );
    }

    @Test
    @DisplayName("모든 자격증 조회 테스트")
    void findAllCertificationsTest() {
        // given
        List<Certification> mockCertifications = Arrays.asList(foundationalCert, associateCert);
        when(certificationRepository.findAll()).thenReturn(mockCertifications);

        // when
        List<Certification> certifications = certificationService.findAllCertifications();

        // then
        assertNotNull(certifications);
        assertEquals(2, certifications.size());
        assertEquals("CLF-C01", certifications.get(0).getCode());
        assertEquals("SAA-C03", certifications.get(1).getCode());
    }

    @Test
    @DisplayName("ID로 자격증 조회 테스트")
    void findCertificationByIdTest() {
        // given
        when(certificationRepository.findById(1L)).thenReturn(Optional.of(foundationalCert));
        when(certificationRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<Certification> foundCert = certificationService.findCertificationById(1L);
        Optional<Certification> notFoundCert = certificationService.findCertificationById(999L);

        // then
        assertTrue(foundCert.isPresent());
        assertEquals("CLF-C01", foundCert.get().getCode());
        assertFalse(notFoundCert.isPresent());
    }

    @Test
    @DisplayName("코드로 자격증 조회 테스트")
    void findCertificationByCodeTest() {
        // given
        when(certificationRepository.findByCode("CLF-C01")).thenReturn(Optional.of(foundationalCert));
        when(certificationRepository.findByCode("NOT-EXISTS")).thenReturn(Optional.empty());

        // when
        Optional<Certification> foundCert = certificationService.findCertificationByCode("CLF-C01");
        Optional<Certification> notFoundCert = certificationService.findCertificationByCode("NOT-EXISTS");

        // then
        assertTrue(foundCert.isPresent());
        assertEquals("AWS Certified Cloud Practitioner", foundCert.get().getName());
        assertFalse(notFoundCert.isPresent());
    }

    @Test
    @DisplayName("레벨로 자격증 조회 테스트")
    void findCertificationsByLevelTest() {
        // given
        List<Certification> associateCerts = Arrays.asList(associateCert);
        when(certificationRepository.findByLevel("Associate")).thenReturn(associateCerts);
        when(certificationRepository.findByLevel("NotExistLevel")).thenReturn(Arrays.asList());

        // when
        List<Certification> foundCerts = certificationService.findCertificationsByLevel("Associate");
        List<Certification> notFoundCerts = certificationService.findCertificationsByLevel("NotExistLevel");

        // then
        assertFalse(foundCerts.isEmpty());
        assertEquals(1, foundCerts.size());
        assertEquals("SAA-C03", foundCerts.get(0).getCode());
        assertTrue(notFoundCerts.isEmpty());
    }

    @Test
    @DisplayName("자격증 저장 테스트")
    void saveCertificationTest() {
        // given
        Certification newCert = new Certification(
            "NEW-001", 
            "New Certification", 
            "New description", 
            200.0, 
            "English", 
            "Advanced"
        );
        
        when(certificationRepository.save(any(Certification.class))).thenReturn(newCert);

        // when
        Certification savedCert = certificationService.saveCertification(newCert);

        // then
        assertNotNull(savedCert);
        assertEquals("NEW-001", savedCert.getCode());
        assertEquals("New Certification", savedCert.getName());
        verify(certificationRepository, times(1)).save(any(Certification.class));
    }

    @Test
    @DisplayName("AWS 자격증 정보 가져오기 테스트")
    void fetchAndSaveAwsCertificationsTest() {
        // given
        when(certificationRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<Certification> fetchedCerts = certificationService.fetchAndSaveAwsCertifications();

        // then
        assertNotNull(fetchedCerts);
        assertFalse(fetchedCerts.isEmpty());
        // 최소 10개의 자격증이 하드코딩 되어 있음
        assertTrue(fetchedCerts.size() >= 10);
        verify(certificationRepository, atLeast(10)).save(any(Certification.class));
    }
} 