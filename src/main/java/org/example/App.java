package org.example;

import org.example.config.AppConfig;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App
{
    public static void main( String[] args )
    {
//        SpringApplication.run(App.class, args);
        ApplicationContext context = SpringApplication.run(App.class, args);

        MessageChannel postChannel = context.getBean("postChannel", MessageChannel.class);

        Post post1 = new Post("Async post #1 with #SpringIntegration", Arrays.asList(
                new Comment("Great post!"),
                new Comment("Nice example!")
        ));

        Post post2 = new Post("Async post #2 with #Concurrency", Arrays.asList(
                new Comment("Loving this feature!"),
                new Comment("Great work!")
        ));

        postChannel.send(MessageBuilder.withPayload(post1).build());
        postChannel.send(MessageBuilder.withPayload(post2).build());
//        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
//        PatientRepository patientRepository = context.getBean(PatientRepository.class);
//        MedicalRecordRepository medicalRecordRepository = context.getBean(MedicalRecordRepository.class);
//
//        Patient patient = new Patient();
//        patient.setFirstName("John");
//        patient.setLastName("Doe");
//        patient.setEmail("john.doe@example.com");
//
//        MedicalRecord medicalRecord = new MedicalRecord();
//        medicalRecord.setDiagnosis("Diagnosis: Flu");
//        medicalRecord.setTreatmentPlan("Treatment: Rest");
//
//        patient.setMedicalRecord(medicalRecord);
//
//        // Save patient (which also saves the medical record due to CascadeType.ALL)
//        patientRepository.save(patient);

//        UserRepository userRepository = context.getBean(UserRepository.class);
//        BioRepository bioRepository = context.getBean(BioRepository.class);
//        ProfilePictureRepository profilePictureRepository = context.getBean(ProfilePictureRepository.class);
//
//        List<User> users = new ArrayList<User>();
//        List<Bio> bioList = new ArrayList<Bio>();
//        List<ProfilePicture> profilePictureList = new ArrayList<ProfilePicture>();
//
//        Bio bio1 = new Bio();
//        bio1.setBioData("I'm a software engineer");
//        bioList.add(bio1);
//
//        ProfilePicture profilePicture1 = new ProfilePicture();
//        profilePicture1.setImageUrl("https://unsplash.com/photos/a-painting-of-a-red-white-and-blue-object-LUz8F-LoRlc");
//        profilePictureList.add(profilePicture1);
//
//        ProfilePicture profilePicture2 = new ProfilePicture();
//        profilePicture2.setImageUrl("https://unsplash.com/photos/a-painting-of-a-red-white-and-blue-object-LUz8F-LoRlc");
//        profilePictureList.add(profilePicture2);
//
//        Bio bio3 = new Bio();
//        bio3.setBioData("I'm a teacher");
//        bioList.add(bio3);
//
//        User user1 = new User();
//        user1.setUsername("harshpatel");
//        user1.setBio(bio1);
//        user1.setProfilePicture(profilePicture1);
//        users.add(user1);
//
//        User user2 = new User();
//        user2.setUsername("smitshah");
//        user2.setProfilePicture(profilePicture2);
//        users.add(user2);
//
//        User user3 = new User();
//        user3.setUsername("harshsolanki");
//        user3.setBio(bio3);
//        users.add(user3);

//        bioRepository.saveAll(bioList);
//        profilePictureRepository.saveAll(profilePictureList);
//        userRepository.saveAll(users);
//        
//        
//            PostRepository postRepository = context.getBean(PostRepository.class);
//            CommentRepository commentRepository = context.getBean(CommentRepository.class);
//            HashtagRepository hashtagRepository = context.getBean(HashtagRepository.class);
//            
//            List<Comment> comments = new ArrayList<Comment>();
//            List<Hashtag> hashtags = new ArrayList<Hashtag>();
//            Post post1 = new Post();
//            post1.setTitle("First post");
//            post1.setContent("This is my first post!");
//            postRepository.save(post1);
//            
//            Post post2 = new Post();
//            post2.setTitle("Second post");
//            post2.setContent("This is my second post!");
//            postRepository.save(post2);
//            
//            Post post3 = new Post();
//            post3.setTitle("Third post");
//            post3.setContent("This is my third post!");
//            postRepository.save(post3);
//
//            Comment comment1 = new Comment("Great post!");
//            comment1.setPost(post1);
//            Comment comment2 = new Comment("Thanks for sharing!");
//            comment2.setPost(post1);
//            Comment comment3 = new Comment("Nice post!");
//            comment3.setPost(post2);
//            Comment comment4 = new Comment("Thanks for sharing!");
//            comment4.setPost(post3);
//
//            comments.add(comment1);
//            comments.add(comment2);
//
//            Hashtag hashtag1 = new Hashtag();
//            hashtag1.setName("#sunset");
//    
//            Hashtag hashtag2 = new Hashtag();
//            hashtag2.setName("#beach");
//    
//            Hashtag hashtag3 = new Hashtag();
//            hashtag3.setName("#pizza");
//    
//            Hashtag hashtag4 = new Hashtag();
//            hashtag4.setName("#hiking");
//    
//            Hashtag hashtag5 = new Hashtag();
//            hashtag5.setName("#mountains");
//
//            hashtags.add(hashtag1);
//            hashtags.add(hashtag2);
//            hashtags.add(hashtag3);
//            hashtags.add(hashtag4);
//            hashtags.add(hashtag5);
//
//            hashtag1.getPosts().add(post1);
//            hashtag2.getPosts().add(post1);
//    
//            hashtag3.getPosts().add(post2);
//    
//            hashtag4.getPosts().add(post3);
//            hashtag5.getPosts().add(post3);
//            
//            commentRepository.saveAll(comments);
//            hashtagRepository.saveAll(hashtags);
    }
}
