// Copyright 2023 Prokhor Kalinin
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package downfall.fx.fetcher;

import downfall.realm.template.VisualMaterialTemplate;
import downfall.util.Configurator;

/**
 * Simple implementation of Fetcher class that returns a new instance of VisualMaterialTemplate on request
 */
public class SimpleMaterialTemplateFetcher implements Fetcher<VisualMaterialTemplate>{
    /**
     * This method is used to return a new instance of VisualMaterialTemplate on request.
     * @return a new instance of VisualMaterialTemplate with its id set to be one larger than the last VisualMaterialTemplate in the currently selected rules
     */
    @Override
    public VisualMaterialTemplate retrieve() {
        VisualMaterialTemplate template = new VisualMaterialTemplate();
        //TODO:Replace this mess with a proper solution distributing IDs.
        //if there are any MaterialTemplates in the current rules
        if(Configurator.getInstance().getRules().getMaterialTemplates().size() > 1)
            //set the new instance's id to be equal of the last item in that list incremented by one
            template.setId(Configurator.getInstance().getRules().getMaterialTemplates().get(Configurator.getInstance().getRules().getMaterialTemplates().size()-1).getId()+1);
        else
            template.setId(1);
        template.setName("New Template");
        return template;
    }
}
