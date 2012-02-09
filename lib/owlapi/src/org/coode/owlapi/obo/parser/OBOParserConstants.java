/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Generated By:JavaCC: Do not edit this line. OBOParserConstants.java */
package org.coode.owlapi.obo.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface OBOParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int LINE_COMMENTED_OUT = 5;
  /** RegularExpression Id. */
  int OPEN_SQUARE_BRACKET = 6;
  /** RegularExpression Id. */
  int STANZA_TYPE = 7;
  /** RegularExpression Id. */
  int CLOSE_SQUARE_BRACKET = 8;
  /** RegularExpression Id. */
  int TAG_NAME = 9;
  /** RegularExpression Id. */
  int TAG_COLON = 10;
  /** RegularExpression Id. */
  int TAG_VALUE = 11;
  /** RegularExpression Id. */
  int TAG_VALUE_END = 12;
  /** RegularExpression Id. */
  int COMMENT_START = 13;
  /** RegularExpression Id. */
  int COMMENT = 14;
  /** RegularExpression Id. */
  int COMMENT_END = 15;
  /** RegularExpression Id. */
  int ERROR = 16;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int IN_STANZA_HEADER = 1;
  /** Lexical state. */
  int IN_TAG_VALUE_PAIR = 2;
  /** Lexical state. */
  int IN_TAG_VALUE = 3;
  /** Lexical state. */
  int IN_COMMENT = 4;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\t\"",
    "\" \"",
    "<LINE_COMMENTED_OUT>",
    "\"[\"",
    "<STANZA_TYPE>",
    "\"]\"",
    "<TAG_NAME>",
    "<TAG_COLON>",
    "<TAG_VALUE>",
    "<TAG_VALUE_END>",
    "\"!\"",
    "<COMMENT>",
    "<COMMENT_END>",
    "<ERROR>",
  };

}
