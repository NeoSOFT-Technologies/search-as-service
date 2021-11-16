package com.solr.clientwrapper.usecase.sampleentity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.solr.clientwrapper.domain.dto.SampleEntityDTO;
import com.solr.clientwrapper.domain.port.api.SampleEntityServicePort;
import com.solr.clientwrapper.domain.port.spi.SampleEntityPersistencePort;
import com.solr.clientwrapper.infrastructure.entity.SampleEntity;
import com.solr.clientwrapper.mapper.SampleEntityMapper;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class DeleteSampleEntityTest {
	
	private static final String DEFAULT_LOGIN = "johndoe";
	
    private SampleEntityMapper sampleEntityMapper;
    private SampleEntity sampleEntity;
    private SampleEntityDTO sampleEntityDto;
    
    @Autowired
    @MockBean
    private SampleEntityServicePort sampleEntityServicePort;
    
    @MockBean
    private SampleEntityPersistencePort sampleEntityPersistencePort;
    
    @InjectMocks
    private DeleteSampleEntity deleteSampleEntity;

	@BeforeEach
    public void init() {
		sampleEntity = new SampleEntity();
		sampleEntity.setId(99l);
		sampleEntity.setAge(20);
		sampleEntity.setName("Test Sample");
		sampleEntity.setPhone(2848);
		sampleEntity.setPassword("Test@123");

        sampleEntityDto = new SampleEntityDTO(sampleEntity);
        deleteSampleEntity = new DeleteSampleEntity(sampleEntityServicePort);
    }
    
	@Test
	void contextLoads() {
		assertThat(sampleEntityServicePort).isNotNull();
	}
	
    @Test
    void deleteSampleEntity() {
    	Mockito.doNothing().when(sampleEntityServicePort)
		.deleteById(sampleEntity.getId());
    	deleteSampleEntity.deleteById(sampleEntity.getId());
    }
 
}
