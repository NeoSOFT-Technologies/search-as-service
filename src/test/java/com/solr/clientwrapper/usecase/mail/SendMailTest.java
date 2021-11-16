package com.solr.clientwrapper.usecase.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.solr.clientwrapper.domain.dto.AdminUserDTO;
import com.solr.clientwrapper.domain.port.api.MailServicePort;
import com.solr.clientwrapper.domain.port.api.UserServicePort;
import com.solr.clientwrapper.domain.port.spi.UserPersistencPort;
import com.solr.clientwrapper.infrastructure.entity.User;
import com.solr.clientwrapper.mapper.UserMapper;

//@WebMvcTest
//@AutoConfigureMockMvc
//@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
//@SpringBootTest

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class SendMailTest {
	
	private static final String DEFAULT_LOGIN = "johndoe";
	
    private UserMapper userMapper;
    private User user;
//    private Mail mail;
    private AdminUserDTO userDto;
    
    @Autowired
    @MockBean
    private UserServicePort userServicePort;
    
    @Autowired
    @MockBean
    private MailServicePort mailServicePort;
    
    @MockBean
    private UserPersistencPort userPersistencePort;
    
    @InjectMocks
    private SendMail sendMail;

	@BeforeEach
    public void init() {
        userMapper = new UserMapper();
        user = new User();
        user.setLogin(DEFAULT_LOGIN);
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail("johndoe@localhost");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setImageUrl("image_url");
        user.setLangKey("en");

        userDto = new AdminUserDTO(user);
        sendMail = new SendMail(mailServicePort);
    }
    
	@Test
	void contextLoads() {
		assertThat(mailServicePort).isNotNull();
	}
	
  @Test
  void sendActivationMailTest() {
  	Mockito.doNothing().when(mailServicePort)
  			.sendActivationEmail(user);
  	sendMail.sendActivationEmail(user);
  }
  
  @Test
  void sendCreationMailTest() {
  	Mockito.doNothing().when(mailServicePort)
  			.sendCreationEmail(user);
  	sendMail.sendCreationEmail(user);
  }
  
  @Test
  void sendPasswordResetMailTest() {
  	Mockito.doNothing().when(mailServicePort)
  			.sendPasswordResetMail(user);
  	sendMail.sendPasswordResetMail(user);
  }
	
//    @Test
//    void sendMailTest() {
//    	Mockito.doNothing().when(mailServicePort)
//    			.sendMail();
//    	sendEmail.sendEmail(mail.getTo(), mail.getSubject(), mail.getContent(), 
//    			mail.isMultipart(), mail.isHtml());
//    }
    
}
