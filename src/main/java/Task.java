import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskId;
    private Date dateOfCreation;
    private Date deadline;
    private String name;
    private String description;

    @OneToMany(mappedBy = "task")
    private List<Subtask> subtasks;
    private String category;
}
