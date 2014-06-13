/*
 * Copyright 2014 FloH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hsosnabrueck.ecs.richwps.wpsmonitor.presentation.converter;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FloH
 */
public class MyConverter implements EntityConverter {
    private List<AbstractQosEntity> datas;
    
    public MyConverter() {
        datas = new ArrayList<AbstractQosEntity>();
    }

    @Override
    public void add(AbstractQosEntity data) {
        datas.add(data);
    }

    @Override
    public List<Object> getPresentateObject() {
        ExampleQos q;
        Integer sum = 0;
        
        List<Object> x = new ArrayList<Object>();
        
        for(AbstractQosEntity e : datas) {
            q = (ExampleQos)e;
            
            sum += q.getValue();
        }
        
        x.add(new MyPresentate(sum));
        x.add(new MyPresentate(sum));
        x.add(new MyPresentate(sum));
        x.add(new MyPresentate(sum));
        
        return x;
    }
    
}