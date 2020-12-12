/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.CommentDAL;
import entity.Comment;
import entity.Post;
import entity.RedditAccount;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

public class CommentLogic extends GenericLogic<Comment, CommentDAL> {

    public final String REPLYS = "replys";
    public final String IS_REPLY = "is_reply";
    public final String POINTS = "points";
    public final String CREATED = "created";
    public final String TEXT = "text";
    public final String ID = "id";
    public final String UNIQUE_ID = "unique_id";
    public final String REDDIT_ACCOUNT_ID = "reddit_account_id";
    public final String POST_ID = "post_id";

    CommentLogic() {
        super(new CommentDAL());
    }

    public List<Comment> getAll() {
        return get(() -> dal().findAll());
    }

    public Comment getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public List<Comment> getCommentsWithText(String text) {
        return get(() -> dal().findByText(text));
    }

    public Comment getCommentWithUniqueId(String uniqueId) {
        return get(() -> dal().findByUniqueId(uniqueId));
    }

    public List<Comment> getCommentsWithCreated(Date created) {
        return get(() -> dal().findByCreated((created)));
    }

    public List<Comment> getCommentsWithPoints(int points) {
        return get(() -> dal().findPoints(points));

    }

    public List<Comment> getCommentsWithReplys(int replys) {
        return get(() -> dal().findByReplys(replys));
    }

    public List<Comment> getCommentsWithReplys(boolean isReply) {
        return get(() -> dal().findByIsReply(isReply));
    }

    @Override
    public List<Comment> search(String search) {
        return get(() -> dal().findContaining(search));
    }

    public Comment createEntity(Map<String, String[]> parameterMap) {

        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");

        Comment entity = new Comment();

        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                String error = "";
                if (value == null || value.trim().isEmpty()) {
                    error = "value cannot be null or empty: " + value;
                }
                if (value.length() > length) {
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException(error);
            }
        };

        String unique_id = parameterMap.get(UNIQUE_ID)[0];
        String text = parameterMap.get(TEXT)[0];

        if (parameterMap.containsKey(POINTS)) {
            try {
                entity.setPoints(Integer.parseInt(parameterMap.get(POINTS)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
        if (parameterMap.containsKey(REPLYS)) {
            try {
                entity.setReplys(Integer.parseInt(parameterMap.get(REPLYS)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        validator.accept(unique_id, 10);
        entity.setUniqueId(unique_id);
        validator.accept(text, 1000);
        entity.setText(text);

        if (parameterMap.containsKey(CREATED)) {
            try {
                entity.setCreated(new SimpleDateFormat("yyyyMMdd").parse(parameterMap.get(CREATED)[0]));
            } catch (ParseException ex) {
                entity.setCreated(Date.from(Instant.now(Clock.systemDefaultZone())));
            }
        }

        if (parameterMap.containsKey(IS_REPLY)) {
            try {
                entity.setIsReply(Boolean.parseBoolean(parameterMap.get(IS_REPLY)[0]));
            } catch (Exception x) {
                throw new ValidationException(x);
            }
        }

        return entity;
    }

    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Reddit_Account_ID", "Post_ID", "Unique_ID", "Text", "Created", "Points", "Replys", "Is_Reply");
    }

    public List<String> getColumnCodes() {
        return Arrays.asList(ID, REDDIT_ACCOUNT_ID, POST_ID, UNIQUE_ID, TEXT, CREATED, POINTS, REPLYS, IS_REPLY);
    }

    public List<?> extractDataAsList(Comment c) {
        return Arrays.asList(c.getId(), c.getRedditAccountId().getId(), c.getPostId().getId(), c.getUniqueId(), c.getText(), c.getCreated(), c.getPoints(), c.getReplys(), c.getIsReply());
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
