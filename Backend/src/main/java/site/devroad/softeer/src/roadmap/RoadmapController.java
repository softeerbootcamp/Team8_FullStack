package site.devroad.softeer.src.roadmap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.src.course.service.CourseService;
import site.devroad.softeer.src.course.service.SubjectService;
import site.devroad.softeer.src.roadmap.dto.GetCourseDetailRes;
import site.devroad.softeer.src.roadmap.dto.GetRoadmapDetailRes;
import site.devroad.softeer.src.roadmap.dto.GetSubjectDetailRes;
import site.devroad.softeer.src.roadmap.dto.subdto.ChapterDetail;
import site.devroad.softeer.src.roadmap.dto.subdto.CourseDetail;
import site.devroad.softeer.utility.JwtUtility;

import java.util.List;
import java.util.Map;

@RestController
public class RoadmapController {

    private final RoadmapService roadmapService;
    private final SubjectService subjectService;
    private final CourseService courseService;

    public RoadmapController(RoadmapService roadmapService, SubjectService subjectService, CourseService courseService) {
        this.roadmapService = roadmapService;
        this.subjectService = subjectService;
        this.courseService = courseService;
    }

    @GetMapping("/api/roadmap")
    public ResponseEntity<?> getRoadmapSubjects(@RequestHeader(value = "jwt") String jwt) {
        try {
            JwtUtility.validateToken(jwt);
            Long accountId = JwtUtility.getAccountId(jwt);
            Map<String, List<List<Object>>> subjects = roadmapService.getSubjects(accountId);
            return new ResponseEntity<>(new GetRoadmapDetailRes(subjects), HttpStatus.OK);
        } catch (CustomException e) {
            return e.getResponseEntity();
        }
    }

    @GetMapping("/api/subject/{subjectId}")
    public ResponseEntity<?> getSubjectDetail(@RequestHeader(value = "jwt") String jwt, @PathVariable("subjectId") String subjectId) {
        try {
            JwtUtility.validateToken(jwt);
            Long accountId = JwtUtility.getAccountId(jwt);
            List<CourseDetail> courses = subjectService.getCourseDetails(Long.valueOf(subjectId), accountId);
            return new ResponseEntity<>(new GetSubjectDetailRes(courses), HttpStatus.OK);
        } catch (CustomException e) {
            return e.getResponseEntity();
        }
    }

    @GetMapping("/api/course/{courseId}")
    public ResponseEntity<?> getCourseDetail(@RequestHeader(value = "jwt") String jwt, @PathVariable("courseId") String courseId) {
        try {
            JwtUtility.validateToken(jwt);
            Long accountId = JwtUtility.getAccountId(jwt);
            List<ChapterDetail> chapterDetails = courseService.getChapterDetails(Long.valueOf(courseId), accountId);
            Long curChapterId = roadmapService.getCurChapterId(accountId);
            return new ResponseEntity<>(new GetCourseDetailRes(chapterDetails, curChapterId), HttpStatus.OK);
        } catch (CustomException e) {
            return e.getResponseEntity();
        }
    }
}
