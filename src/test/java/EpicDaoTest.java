import com.example.TaskTrackerApplication;
import com.example.dao.EpicDao;
import com.example.exceptions.EpicNotFoundException;
import com.example.exceptions.TaskNotFoundException;
import com.example.model.Epic;
import com.example.model.Task;
import com.example.model.TaskStatus;
import com.example.model.TaskType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource("/application-test.properties")
@SpringBootTest(classes = TaskTrackerApplication.class)
@Sql({"classpath:schema.sql", "classpath:data.sql"})
//@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EpicDaoTest {

    private final EpicDao epicDao;

    @Autowired
    public EpicDaoTest(EpicDao epicDao) {
        this.epicDao = epicDao;
    }


    @Test
    public void contextLoads(){
        assertNotNull(epicDao);
    }

    @Test
    public void getAllTest(){
        assertEquals(1, epicDao.getAll().size());

    }

    @Test
    public void getByIdTest(){
        assertNotNull(epicDao.getById(1));
        assertEquals("EPIC Test", epicDao.getById(1).getName());
        assertEquals(1, epicDao.getById(1).getId());
    }

    @Test
    public void getByIdNotExistTest(){
        assertThrows(EpicNotFoundException.class,
                ()->{
                    epicDao.getById(10) ;
                });
    }

    @Test
    public void createTest(){
        Epic epic = Epic.builder()
                .name("EpicCreated")
                .description("EpicCreated description")
                .status(TaskStatus.NEW)
                .type(TaskType.EPIC)
                .startTime(LocalDateTime.of(2023, 05, 14, 12, 00, 00))
                .endTime(LocalDateTime.of(2023, 05, 14, 13, 00, 00))
                .duration(60)
                .build();
        Epic epicCreated = epicDao.create(epic);
        assertEquals(2, epicCreated.getId());
        assertEquals("EpicCreated", epicCreated.getName());
        assertEquals("EpicCreated description", epic.getDescription());
    }

    @Test
    public void updateTest(){
        Epic epic = Epic.builder()
                .id(1)
                .name("EpicUPD")
                .description("EpicUPD description")
                .status(TaskStatus.NEW)
                .type(TaskType.EPIC)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .duration(60)
                .build();
        Epic epicUpd = epicDao.update(epic);
        assertEquals(epicUpd, epic);
    }

    @Test
    public void updateNotExistTest(){
        Epic epic = Epic.builder()
                .id(2)
                .name("EpicUPD")
                .description("EpicUPD description")
                .status(TaskStatus.NEW)
                .type(TaskType.EPIC)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .duration(60)
                .build();
        assertThrows(EpicNotFoundException.class,
                ()->{
                    epicDao.update(epic) ;
                });
    }

    @Test
    public void deleteByIdTest(){
        epicDao.deleteById(1);
        assertThrows(EpicNotFoundException.class,
                ()->{
                    epicDao.getById(1) ;
                });
        assertEquals(0, epicDao.getAll().size());
    }

    @Test
    public void deleteAllTest(){
        epicDao.deleteAll();
        assertEquals(0, epicDao.getAll().size());
    }
}
