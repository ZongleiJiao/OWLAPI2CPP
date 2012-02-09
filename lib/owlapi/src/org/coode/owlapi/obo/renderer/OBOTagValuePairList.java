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

package org.coode.owlapi.obo.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.owlapi.obo.parser.OBOVocabulary;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;

/**
 * Author: Nick Drummond<br>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 19, 2008<br><br>
 * <p/>
 * An ordered rendering of the Tag Value Pairs that also supports:
 * - default values
 * - unknown tags (which are rendered at the end of the known tags)
 * - extraction of TVPs from annotations
 */
public class OBOTagValuePairList {


    private Map<String, Set<String>> knownTVPs = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> unknownTVPs = new HashMap<String, Set<String>>();

    private List<OBOVocabulary> vocab;

    private IRIShortFormProvider iriSFP;

    private Map<IRI, String> defaults = new HashMap<IRI, String>();

    private Writer writer;


    /**
     * @param knownVocab the set of tags that are known by this generator
     */
    public OBOTagValuePairList(List<OBOVocabulary> knownVocab) {
        this.vocab = knownVocab;
        iriSFP = new SimpleIRIShortFormProvider();
    }


    public void visit(OWLAnnotation annot) {
        addPair(annot.getProperty().getIRI(), ((OWLLiteral) annot.getValue()).getLiteral());
    }


    public void addPair(OBOVocabulary tag, String value) {
        addPair(tag.getIRI(), value);
    }


    public void addPair(IRI tag, String value) {
        boolean found = false;
        for (OBOVocabulary obo : vocab) {
            if (tag.equals(obo.getIRI())) {
                addPair(obo.getName(), value, knownTVPs);
                found = true;
                break;
            }
        }
        if (!found) {
            final String name = iriSFP.getShortForm(tag);
            addPair(name, value, unknownTVPs);
        }
    }


    public void setPair(OBOVocabulary key, String value) {
        knownTVPs.remove(key.getName());
        addPair(key.getIRI(), value);
    }


    public void setDefault(OBOVocabulary tag, String value) {
        defaults.put(tag.getIRI(), value);
    }


    public void setDefault(IRI tag, String value) {
        defaults.put(tag, value);
    }


    public Set<String> getValues(OBOVocabulary key) {
        Set<String> values = knownTVPs.get(key.getName());
        if (values == null) {
            values = Collections.emptySet();
        }
        return values;
    }


    private void addPair(String tag, String value, Map<String, Set<String>> map) {
        Set<String> set = map.get(tag);
        if (set == null) {
            set = new HashSet<String>(1);
            map.put(tag, set);
        }
        set.add(value);
    }


    public void write(Writer writer) {
        this.writer = writer;

        // write tags out in order
        for (OBOVocabulary tag : vocab) {
            Set<String> values = knownTVPs.get(tag.getName());
            if (values == null) {
                String def = defaults.get(tag.getIRI());
                if (def != null) {
                    values = Collections.singleton(def);
                }
            }
            if (values != null) {
                for (String value : values) {
                    writeTagValuePair(tag, value);
                }
            }
        }

        // write additional tags in no specified order
        for (String unknownTag : unknownTVPs.keySet()) {
            for (String value : unknownTVPs.get(unknownTag)) {
                writeTagValuePair(unknownTag, value);
            }
        }
    }


    private void writeTagValuePair(OBOVocabulary key, String value) {
        writeTagValuePair(key.getName(), value);
    }


    private void writeTagValuePair(String key, String value) {
        if (key != null && value != null) {
            write(key);
            write(": ");
            write(value);
            writeNewLine();
        }
    }


    private void writeNewLine() {
        write("\n");
    }


    private void write(String s) {
        try {
            writer.write(s);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
