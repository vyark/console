import javax.persistence.*;

@Entity
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long subtaskId;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Task task;
}
