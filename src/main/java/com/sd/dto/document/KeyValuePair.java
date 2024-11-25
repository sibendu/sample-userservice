package com.sd.dto.document;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class KeyValuePair implements Serializable {
	private Key key;
	private Value value;
}
