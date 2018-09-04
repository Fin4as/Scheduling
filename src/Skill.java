/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a skill
 *
 * @author Hayat
 */
public class Skill {

    private String skillID; // ID to identify a specific skill
    private String description; // variable that provide a description of the skill, not used in the code
    private String prevTaskID; // variable not used in the code
    private List<Resource> listResource; // a skill has a list of object resources

    /**
     * Constructor whith inistializes variables and create a list of Resources
     *
     * @param s is a skill
     * @param d is the description of this skill
     * @param p is a previousTask of this skill
     *
     */
    public Skill(String s, String d, String p) {
        skillID = s;
        description = d;
        prevTaskID = p;
        listResource = new ArrayList<Resource>();
    }

    /**
     *This method returns the skillID
     * @return skillID
     */
    public String getSkillID() {
        return this.skillID;
    }

    /**
     *This method returns the list of resources of this skill
     * @return list of resources of this skill
     */
    public List<Resource> getListResource() {
        return this.listResource;
    }

    /**
     * Method that adds a resource in the list of resources
     *
     * @param r os a resource
     */
    public void addResource(Resource r) {
        listResource.add(r);
    }

    /**
     * Method used in case of non waiting tasks, it needs a srtart time and a
     * duration of the task
     *
     * @param startTime the time when a task starts
     * @param avTime  the duration  of a task
     * @return resource which represents the index in the list of the resource
     * available
     */
    public int getStrictestAvailable(int startTime, int avTime) {
        int resource = -1;
        for (int j = 0; j < listResource.size(); j++) {
            boolean available = listResource.get(j).isAvailable(startTime, avTime);
            if (available) {
                resource = j;
                break;
            }
        }
        return resource;
    }

    /**
     * Method to get the resource the fastest available to perfom a task which
     * starts at startTime and lasts avTime
     *
     * @param startTime when the task starts
     * @param avTime is the duration of the task
     * @return an index corresponding to the resource (in the list of resource)who is available
     */
    public int getFastestAvailable(int startTime, int avTime) {
        int resource = -1;
        for (int j = 0; j < listResource.size(); j++) {
            int min = listResource.get(j).getNextAvailableTime(startTime, avTime);
            if (listResource.size() == 1 && min != -1) {
                resource = 0;
            } else if (listResource.size() > 1) {
                if (min != -1 && resource == -1) {
                    resource = j;
                }
                for (int i = j + 1; i < listResource.size(); i++) {
                    int currentTime = listResource.get(i).getNextAvailableTime(startTime, avTime);

                    if (min != -1 && currentTime < min) {
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

        }
        return resource;
    }
}
