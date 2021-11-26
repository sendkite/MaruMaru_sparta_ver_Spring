package com.example.marumaru_sparta_verspring.service;

import com.example.marumaru_sparta_verspring.domain.S3Uploader;
import com.example.marumaru_sparta_verspring.domain.posts.Meet;
import com.example.marumaru_sparta_verspring.dto.MeetRequestDto;
import com.example.marumaru_sparta_verspring.repository.MeetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final S3Uploader s3Uploader;

    public Meet saveMeet(MeetRequestDto meetRequestDto) {

        Meet meet = new Meet();
        meet.setTitle(meetRequestDto.getTitle());
        meet.setContent(meetRequestDto.getContent());
        meet.setImage(meetRequestDto.getImage());
        meet.setContent(meetRequestDto.getContent());
        meetRepository.save(meet);
        return meet;
    }
}
