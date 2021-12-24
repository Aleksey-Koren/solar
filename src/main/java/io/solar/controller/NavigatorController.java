package io.solar.controller;

import io.solar.dto.CourseDto;
import io.solar.entity.Course;
import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import io.solar.facade.CourseFacade;
import io.solar.service.CourseService;
import io.solar.service.NavigatorService;
import io.solar.service.UserService;
import io.solar.service.scheduler.ObjectCoordinatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/navigate")
public class NavigatorController {

    private final NavigatorService navigatorService;
    private final ObjectCoordinatesService objectCoordinatesService;
    private final CourseFacade courseFacade;
    private final CourseService courseService;
    private final UserService userService;


    @PostMapping("/dock")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void dockShip(@RequestParam Long stationId,
                         @RequestParam Long shipId) {

        navigatorService.dockShip(stationId, shipId);
    }


    @PostMapping("/undock")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void undockShip(@RequestParam Long shipId) {

        navigatorService.undockShip(shipId);
    }

    @PostMapping("/course")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void layCourse(@RequestBody CourseDto dto, Principal principal) {
        User authUser = userService.findByLogin(principal.getName());

        if (!userService.isUserLocatedInObject(authUser, dto.getObjectId())) {
            userCantException(authUser.getLocation().getId(), dto.getObjectId());
        }

        courseFacade.updateCourseChain(dto);
    }

    @DeleteMapping("/course/{id}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void deleteCourse(@PathVariable("id") Long id, Principal principal) {
        Course course = courseService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("There is no course with such id in database. Course id = %d", id)));
        BasicObject object = course.getObject();
        User authUser = userService.findByLogin(principal.getName());

        if (!userService.isUserLocatedInObject(authUser, object.getId())) {
            userCantException(authUser.getLocation().getId(), object.getId());
        }

        courseFacade.deleteCourse(course);
    }


    @Scheduled(fixedDelayString = "${app.navigator.update_coordinates_delay}")
    public void updateObjectsCoordinate() {
        objectCoordinatesService.update();
    }


    private void userCantException(Long locationId, Long objectId) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                String.format("User can change course only for object where he is located in. User location_id = %d. " +
                        "Object's to change course id = %d", locationId, objectId));
    }
}