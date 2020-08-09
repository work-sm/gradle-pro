package com.sam.demo.perform.service.tle;

import com.sam.demo.perform.script.Story;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TleStory extends Story {
    private String param;
    private String result;
}
