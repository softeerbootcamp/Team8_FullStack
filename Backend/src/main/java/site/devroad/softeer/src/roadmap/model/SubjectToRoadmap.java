package site.devroad.softeer.src.roadmap.model;

public class SubjectToRoadmap {
    private Long id;
    private Long roadmapId;
    private Long subjectId;
    private Long sequence;

    public SubjectToRoadmap(Long id, Long roadmapId, Long subjectId, Long sequence) {
        this.id = id;
        this.roadmapId = roadmapId;
        this.subjectId = subjectId;
        this.sequence = sequence;
    }

    public Long getId() {
        return id;
    }

    public Long getRoadmapId() {
        return roadmapId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public Long getSequence() {
        return sequence;
    }
}
