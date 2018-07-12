/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hayat
 */
public class Skill {

    private String skillID;
    private String description;
    private String prevTaskID;
    private List<Resource> listResource;

    public Skill(String s, String d, String p) {
        skillID = s;
        description = d;
        prevTaskID = p;
        listResource = new ArrayList<Resource>();
    }

    public String getSkillID() {
        return this.skillID;
    }

    public List<Resource> getListResource() {
        return this.listResource;
    }

    public void addResource(Resource r) {
        listResource.add(r);
    }
    
       public int getStrictestAvailable(int startTime, int avTime) {
        int resource = -1;
        for (int j = 0; j < listResource.size(); j++) {
            boolean available = listResource.get(j).isAvailable(startTime, avTime);
            if(available){
                resource=j;
                break;
            }
        }
        return resource;
    }

    public int getFastestAvailable(int startTime, int avTime) {
        int resource = -1;
        for (int j = 0; j < listResource.size(); j++) {
            int min = listResource.get(j).getNextAvailableTime(startTime, avTime);
            if (listResource.size() == 1 && min != -1) {
                resource = 0;
            } else if (listResource.size() > 1) {
                for (int i = j+1; i < listResource.size(); i++) { 
                    int currentTime = listResource.get(i).getNextAvailableTime(startTime, avTime);
                    if (min != -1 && currentTime <= min) {
                        resource = i;

                    } else if (min == -1) {
                        if (currentTime == -1 && i == listResource.size() - 1) {
                            resource = -1;
                        } else if (currentTime != -1) {
                            min = currentTime;
                            resource = i;
                        }
                    }

                }

            }
            
        }return resource;
    }
}
