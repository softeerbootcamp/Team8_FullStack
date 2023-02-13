package site.devroad.softeer.src.roadmap.course;

import org.springframework.stereotype.Service;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
import site.devroad.softeer.src.roadmap.chapter.Chapter;
import site.devroad.softeer.src.roadmap.chapter.ChapterRepo;
import site.devroad.softeer.src.roadmap.course.CourseRepo;
import site.devroad.softeer.src.roadmap.dto.subdto.ChapterDetail;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    public static final Long FINISHED = -1L;
    private final ChapterRepo chapterRepo;
    private final CourseRepo courseRepo;

    public CourseService(ChapterRepo chapterRepo, CourseRepo courseRepo) {
        this.chapterRepo = chapterRepo;
        this.courseRepo = courseRepo;
    }

    public List<ChapterDetail> getChapterDetails(Long courseId){
        return chapterRepo.findChapterDetailByCourseId(courseId);
    }

    public Optional<Chapter> getNextChapter(Long chapterId){
        Optional<Chapter> chapterById = chapterRepo.findChapterById(chapterId);
        if (chapterById.isEmpty()) {
            throw new CustomException(ExceptionType.CHAPTER_NOT_FOUND);
        }
        Chapter chapter = chapterById.get();
        Long courseId = chapter.getCourseId();
        Integer sequence = chapter.getSequence();
        return chapterRepo.findNextChapter(courseId, sequence + 1);
    }

    public Chapter getChapter(Long chapterId){
        Optional<Chapter> chapterById = chapterRepo.findChapterById(chapterId);
        if (chapterById.isEmpty()) {
            throw new CustomException(ExceptionType.CHAPTER_NOT_FOUND);
        }
        return chapterById.get();
    }

    public ChapterDetail getChapterDetail(Long chapterId){
        Optional<ChapterDetail> chapterById = chapterRepo.findChapterDetailById(chapterId);
        if (chapterById.isEmpty()) {
            throw new CustomException(ExceptionType.CHAPTER_NOT_FOUND);
        }
        return chapterById.get();
    }

    public Boolean getCourseFinished(Long chapterId){
        Optional<Chapter> nextChapter = getNextChapter(chapterId);
        return nextChapter.isEmpty();
    }

    public Long getNextChapterId(Long chapterId){
        Optional<Chapter> nextChapter = getNextChapter(chapterId);
        if (nextChapter.isPresent()) {
            return nextChapter.get().getId();
        }
        return FINISHED;
    }
}