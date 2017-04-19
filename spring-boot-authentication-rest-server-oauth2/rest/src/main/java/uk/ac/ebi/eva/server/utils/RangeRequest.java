package uk.ac.ebi.eva.server.utils;

import uk.ac.ebi.eva.server.rest.another.ByteGenerator;

/**
 * Created by jorizci on 14/09/16.
 */
public class RangeRequest {

    private final Long start;
    private final Long end;
    private final Long resourceSize;

    public RangeRequest(Long start, Long end, Long resourceSize) {
        this.start = start;
        this.end = end;
        this.resourceSize = resourceSize;
    }

    public RangeRequest(ByteGenerator byteGenerator) {
        start = 0L;
        if (byteGenerator.getSize() != null) {
            end = byteGenerator.getSize() - 1;
            resourceSize = byteGenerator.getSize();
        }else{
            end = null;
            resourceSize = null;
        }
    }

    @Override
    public String toString() {
        return "Start '" + start + "' End '" + end + "' Resource Size '" + resourceSize + "'";
    }

    public String getContentRange() {
        StringBuilder contentRange = new StringBuilder();
        if (start != null) {
            contentRange.append(start);
        }
        contentRange.append("-");
        if (end != null) {
            contentRange.append(end);
        }
        contentRange.append("/");
        if (resourceSize != null) {
            contentRange.append(resourceSize);
        } else {
            contentRange.append("*");
        }
        return contentRange.toString();
    }

    public Long getResourceSize() {
        return resourceSize;
    }

    public Long getStart() {
        return start;
    }

    public Long getEnd() {
        return end;
    }
}
