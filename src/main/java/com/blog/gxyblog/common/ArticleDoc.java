package com.blog.gxyblog.common;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/31 23:52
 * @DESCRIPTION:
 */
public class ArticleDoc {
    public static final String INDICES_NAME = "article";
    public static final String INDICES_FIELD = "all";
    public static final String HIGH_FIELD_TITLE = "title";
    public static final String HIGH_FIELD_CONTENT = "content";
    public static final String HIGH_FIELD_DESCRIPTION = "description";

    public static final String ARTICLE_INDEX_DOC = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"title\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"content\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"image\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"crate_time\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "       \"update_time\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"read_permissions\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"publish\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"views\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"type_id\":{\n" +
            "         \"type\": \"keyword\",\n" +
            "          \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"description\":{\n" +
            "        \"type\": \"text\", \n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "         \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"like_count\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"is_del\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"all\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "        \n" +
            "      }\n" +
            "      \n" +
            "      \n" +
            "    }\n" +
            "  }\n" +
            "}";
}
