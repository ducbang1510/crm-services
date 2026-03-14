/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.TaskDTO;
import com.tdbang.crm.entities.Task;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.TaskPriority;
import com.tdbang.crm.enums.TaskStatus;
import com.tdbang.crm.enums.TaskType;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.TaskMapper;
import com.tdbang.crm.repositories.TaskRepository;
import com.tdbang.crm.repositories.UserRepository;
import com.tdbang.crm.utils.MessageConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
        ReflectionTestUtils.setField(taskService, "taskRepository", taskRepository);
        ReflectionTestUtils.setField(taskService, "userRepository", userRepository);
        ReflectionTestUtils.setField(taskService, "taskMapper", taskMapper);
    }

    @Test
    void createTask_validInput_returnsSuccess() {
        TaskDTO dto = buildTaskDTO("Todo", "CONTACT", 1L);
        User user = buildUser(1L, "Admin");
        Task savedTask = buildTask(10L, user, user);
        TaskDTO resultDto = new TaskDTO();
        resultDto.setPk(10L);

        when(userRepository.findUserByPk(1L)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.mappingTaskEntityToTaskDTO(savedTask)).thenReturn(resultDto);

        ResponseDTO result = taskService.createTask(dto, 1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.CREATING_TASK_SUCCESS, result.getMsg());
    }

    @Test
    void createTask_invalidTaskType_throwsException() {
        TaskDTO dto = buildTaskDTO("INVALID_TYPE", null, null);
        dto.setAssignedToUserFk(1L);

        CRMException ex = assertThrows(CRMException.class,
            () -> taskService.createTask(dto, 1L));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void createTask_standaloneNoEntity_succeeds() {
        TaskDTO dto = buildTaskDTO("Call", null, null);
        User user = buildUser(1L, "Admin");
        Task savedTask = buildTask(10L, user, user);
        savedTask.setEntityType(null);
        savedTask.setEntityFk(null);
        TaskDTO resultDto = new TaskDTO();
        resultDto.setPk(10L);

        when(userRepository.findUserByPk(1L)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.mappingTaskEntityToTaskDTO(savedTask)).thenReturn(resultDto);

        ResponseDTO result = taskService.createTask(dto, 1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
    }

    @Test
    void updateTask_changeStatus_succeeds() {
        User user = buildUser(1L, "Admin");
        Task existingTask = buildTask(10L, user, user);
        TaskDTO dto = new TaskDTO();
        dto.setTitle("Updated Title");
        dto.setTaskType("Todo");
        dto.setAssignedToUserFk(1L);
        dto.setStatus("Done");
        TaskDTO resultDto = new TaskDTO();
        resultDto.setPk(10L);

        when(taskRepository.findByPk(10L)).thenReturn(Optional.of(existingTask));
        when(userRepository.findUserByPk(1L)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);
        when(taskMapper.mappingTaskEntityToTaskDTO(existingTask)).thenReturn(resultDto);

        ResponseDTO result = taskService.updateTask(10L, dto, 1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.UPDATING_TASK_SUCCESS, result.getMsg());
    }

    @Test
    void listMyTasks_paginatedByDueDate() {
        User user = buildUser(1L, "Admin");
        Task task1 = buildTask(1L, user, user);
        Task task2 = buildTask(2L, user, user);
        List<Task> tasks = List.of(task1, task2);
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), 2);

        TaskDTO dto1 = new TaskDTO();
        dto1.setPk(1L);
        TaskDTO dto2 = new TaskDTO();
        dto2.setPk(2L);

        when(taskRepository.findByAssignedToPkOrderByDueDateAsc(eq(1L), any())).thenReturn(taskPage);
        when(taskMapper.mappingToListTaskDTO(tasks)).thenReturn(List.of(dto1, dto2));

        ResponseDTO result = taskService.listMyTasks(1L, 10, 0);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(2L, data.get("totalElements"));
    }

    @Test
    void listTasksByEntity_filteredCorrectly() {
        User user = buildUser(1L, "Admin");
        Task task = buildTask(1L, user, user);
        List<Task> tasks = List.of(task);

        TaskDTO dto = new TaskDTO();
        dto.setPk(1L);

        when(taskRepository.findByEntityTypeAndEntityFkOrderByCreatedOnDesc("CONTACT", 1L)).thenReturn(tasks);
        when(taskMapper.mappingToListTaskDTO(tasks)).thenReturn(List.of(dto));

        ResponseDTO result = taskService.listTasksByEntity("CONTACT", 1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_LIST_OF_TASKS_SUCCESS, result.getMsg());
    }

    @Test
    void getTaskSummary_countsCorrectly() {
        when(taskRepository.countByAssignedToPkAndStatus(1L, TaskStatus.OPEN)).thenReturn(5L);
        when(taskRepository.countByAssignedToPkAndStatus(1L, TaskStatus.IN_PROGRESS)).thenReturn(3L);
        when(taskRepository.countByAssignedToPkAndStatus(1L, TaskStatus.DONE)).thenReturn(10L);

        ResponseDTO result = taskService.getTaskSummary(1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_TASK_SUMMARY_SUCCESS, result.getMsg());
        @SuppressWarnings("unchecked")
        Map<String, Long> data = (Map<String, Long>) result.getData();
        assertEquals(5L, data.get("open"));
        assertEquals(3L, data.get("inProgress"));
        assertEquals(10L, data.get("done"));
    }

    @Test
    void deleteTask_removesFromDb() {
        User user = buildUser(1L, "Admin");
        Task task = buildTask(10L, user, user);

        when(taskRepository.findByPk(10L)).thenReturn(Optional.of(task));

        ResponseDTO result = taskService.deleteTask(10L, 1L);

        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.DELETING_TASK_SUCCESS, result.getMsg());
        verify(taskRepository, times(1)).delete(task);
    }

    // --- Helper methods ---

    private TaskDTO buildTaskDTO(String taskType, String entityType, Long entityFk) {
        TaskDTO dto = new TaskDTO();
        dto.setTitle("Test Task");
        dto.setTaskType(taskType);
        dto.setEntityType(entityType);
        dto.setEntityFk(entityFk);
        dto.setAssignedToUserFk(1L);
        dto.setPriority("Medium");
        return dto;
    }

    private User buildUser(Long pk, String name) {
        User user = new User();
        user.setPk(pk);
        user.setName(name);
        return user;
    }

    private Task buildTask(Long pk, User assignedTo, User createdBy) {
        Task task = new Task();
        task.setPk(pk);
        task.setTitle("Test Task");
        task.setTaskType(TaskType.TODO);
        task.setEntityType("CONTACT");
        task.setEntityFk(1L);
        task.setStatus(TaskStatus.OPEN);
        task.setPriority(TaskPriority.MEDIUM);
        task.setDueDate(new Date());
        task.setDescription("Test description");
        task.setAssignedTo(assignedTo);
        task.setCreatedBy(createdBy);
        return task;
    }
}
