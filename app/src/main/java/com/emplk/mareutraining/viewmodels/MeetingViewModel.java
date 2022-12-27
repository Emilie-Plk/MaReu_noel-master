package com.emplk.mareutraining.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.emplk.mareutraining.models.Meeting;
import com.emplk.mareutraining.models.Room;
import com.emplk.mareutraining.repositories.MeetingsRepository;
import com.emplk.mareutraining.repositories.RoomRepository;
import com.emplk.mareutraining.ui.list.MeetingsViewStateItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MeetingViewModel extends ViewModel {
    @NonNull
    private final MeetingsRepository repository;

    @NonNull
    private final RoomRepository roomRepository;

    public MeetingViewModel(@NonNull MeetingsRepository repository, RoomRepository roomRepository) {
        this.repository = repository;
        this.roomRepository = roomRepository;
    }

    public LiveData<List<MeetingsViewStateItem>> getMeetingViewStateItemsLiveData() {
        return Transformations.map(repository.getMeetingsLiveData(), meetings -> {
                    List<MeetingsViewStateItem> meetingsViewStateItems = new ArrayList<>();
                    for (Meeting meeting : meetings) {
                        meetingsViewStateItems.add(
                                new MeetingsViewStateItem(
                                        meeting.getMeetingTitle(),
                                        meeting.getRoom().getRoomName(),
                                        formatDate(meeting.getDate()),
                                        formatTimeStart(meeting.getTimeStart()),
                                        formatParticipantList(meeting.getParticipants()),
                                        meeting.getRoom().getRoomColor(),
                                        meeting.getId())
                        );
                    }
                    return meetingsViewStateItems;
                }
        );
    }

    public static String formatParticipantList(List<String> participantList) {
        String stringList = participantList.toString()
                .replace("[", "")
                .replace("]", "")
                .replace("@lamzone.fr", "");

        return Arrays.stream(stringList.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    private String formatTimeStart(LocalTime startingTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startingTime.format(formatter);
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    public void onDeleteMeetingClicked(long meetingId) {
        repository.deleteMeeting(meetingId);
    }

    public LiveData<List<Room>> getRooms() {
        return roomRepository.getRooms();
    }


}
