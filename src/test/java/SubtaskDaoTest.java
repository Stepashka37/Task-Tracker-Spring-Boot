import com.example.TaskTrackerApplication;
import com.example.dao.EpicDao;
import com.example.dao.SubtaskDao;
import com.example.exceptions.EpicNotFoundException;
import com.example.exceptions.SubtaskNotFoundException;
import com.example.model.Epic;
import com.example.model.Subtask;
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
public class SubtaskDaoTest {

    private final SubtaskDao subtaskDao;
    private final EpicDao epicDao;

    @Autowired
    public SubtaskDaoTest(SubtaskDao subtaskDao, EpicDao epicDao) {
        this.subtaskDao = subtaskDao;
        this.epicDao = epicDao;
    }


    @Test
    public void contextLoads(){
        assertNotNull(subtaskDao);
    }

    @Test
    public void getAllTest(){
        assertEquals(2, subtaskDao.getAll().size());

    }

    @Test
    public void getByIdTest(){
        assertNotNull(subtaskDao.getById(1));
        assertEquals("SUBTASK1 Test", subtaskDao.getById(1).getName());
        assertEquals(1, subtaskDao.getById(1).getId());
    }

    @Test
    public void getByIdNotExistTest(){
        assertThrows(SubtaskNotFoundException.class,
                ()->{
                    subtaskDao.getById(10) ;
                });
    }

    @Test
    public void createTest(){
        Subtask subtask = Subtask.builder()
                .name("SubtaskCreated")
                .description("SubtaskCreated description")
                .status(TaskStatus.DONE)
                .type(TaskType.SUBTASK)
                .startTime(LocalDateTime.of(2023, 05, 01, 17, 00, 00))
                .endTime(LocalDateTime.of(2023, 05, 01, 17, 30, 00))
                .duration(30)
                .epicId(1)
                .build();
        Subtask subtaskCreated = subtaskDao.create(subtask);
        Epic epicFromDb = epicDao.getById(1);
        assertEquals(3, subtaskCreated.getId());
        assertEquals("SubtaskCreated", subtaskCreated.getName());
        assertEquals("SubtaskCreated description", subtask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, epicFromDb.getStatus());
        assertEquals(90, epicFromDb.getDuration());
        assertEquals(LocalDateTime.of(2023, 05,01,16,00,00), epicFromDb.getStartTime());
        assertEquals(LocalDateTime.of(2023, 05,01,17,30,00), epicFromDb.getEndTime());
    }


    @Test
    public void updateTest(){
        Subtask subtask = Subtask.builder()
                .id(2)
                .name("SubtaskUPD")
                .description("SubtaskUPD description")
                .status(TaskStatus.IN_PROGRESS)
                .type(TaskType.SUBTASK)
                .startTime(LocalDateTime.of(2023, 05,01,16,30,00))
                .endTime(LocalDateTime.of(2023, 05,01,16,50,00))
                .duration(20)
                .epicId(1)
                .build();
        Subtask subtaskUpd = subtaskDao.update(subtask);
        Epic epicFromDb = epicDao.getById(1);
        assertEquals(subtaskUpd, subtask);
        assertEquals(TaskStatus.IN_PROGRESS, epicFromDb.getStatus());
        assertEquals(50, epicFromDb.getDuration());
        assertEquals(LocalDateTime.of(2023, 05,01,16,00,00), epicFromDb.getStartTime());
        assertEquals(LocalDateTime.of(2023, 05,01,16,50,00), epicFromDb.getEndTime());
    }

    @Test
    public void updateNotExistTest(){
        Subtask subtask = Subtask.builder()
                .id(3)
                .name("SubtaskUPD")
                .description("SubtaskUPD description")
                .status(TaskStatus.NEW)
                .type(TaskType.SUBTASK)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .duration(60)
                .build();
        assertThrows(SubtaskNotFoundException.class,
                ()->{
                    subtaskDao.update(subtask) ;
                });
        assertEquals(TaskStatus.NEW, epicDao.getById(1).getStatus());
    }

    @Test
    public void deleteByIdTest(){
        Subtask subtask = Subtask.builder()
                .name("SubtaskCreated")
                .description("SubtaskCreated description")
                .status(TaskStatus.DONE)
                .type(TaskType.SUBTASK)
                .startTime(LocalDateTime.of(2023, 05, 01, 17, 00, 00))
                .endTime(LocalDateTime.of(2023, 05, 01, 17, 30, 00))
                .duration(30)
                .epicId(1)
                .build();
        subtaskDao.create(subtask);
        Epic epicFromDb = epicDao.getById(1);
        assertEquals(TaskStatus.IN_PROGRESS, epicFromDb.getStatus());
        assertEquals(90, epicFromDb.getDuration());
        assertEquals(LocalDateTime.of(2023, 05,01,16,00,00), epicFromDb.getStartTime());
        assertEquals(LocalDateTime.of(2023, 05,01,17,30,00), epicFromDb.getEndTime());

        subtaskDao.deleteById(3);
        Epic epicFromDbAfterDelete = epicDao.getById(1);
        assertEquals(60, epicFromDbAfterDelete.getDuration());
        assertEquals(LocalDateTime.of(2023, 05,01,16,00,00), epicFromDbAfterDelete.getStartTime());
        assertEquals(LocalDateTime.of(2023, 05,01,17,00,00), epicFromDbAfterDelete.getEndTime());
        assertThrows(SubtaskNotFoundException.class,
                ()->{
                    subtaskDao.getById(3) ;
                });
        assertEquals(2, subtaskDao.getAll().size());
        assertEquals(TaskStatus.NEW, epicDao.getById(1).getStatus());
    }

    @Test
    public void deleteAllTest(){
        subtaskDao.deleteAll();
        assertEquals(0, subtaskDao.getAll().size());
    }

    @Test
    public void getEpicSubtasksTest(){
        assertEquals(2, subtaskDao.getEpicSubtasks(1).size());
    }

    @Test
    public void deleteAllSubtasksAndCheckEpicDeleted(){
        subtaskDao.deleteById(1);
        subtaskDao.deleteById(2);
        assertThrows(EpicNotFoundException.class,
                () -> {
                        epicDao.getById(1);
        });

    }

}
