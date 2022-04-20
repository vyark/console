import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
public class Subtask {

    @Id
    private Long subtaskId;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "taskId", nullable = false)
    private Task task;
}
