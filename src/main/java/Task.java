import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@Entity
public class Task {
    @Id
    private Long taskId;
    private Date dateOfCreation;
    private Date deadline;
    private String name;
    private String description;

    @OneToMany(mappedBy = "task")
    private List<Subtask> subtasks;
    private String category;
}
