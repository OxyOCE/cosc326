#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <strings.h>

#define MAX_CLIENT_LENGTH 405 // 100 chars per string, 3 spaces, 1 newline, and 1 null char
#define MAX_CMD_LEN 103 // 1 cmd char, 1 space, 100 char search string, and 1 null char
#define NO_MATCH -1

typedef enum quit_flag_e {RUN, QUIT} quit_flag;
typedef enum attribute_e {FIRST_NAME, LAST_NAME, PHONE_NUMBER, EMAIL_ADDRESS} attribute;

typedef struct client_t
{
    char *first_name;
    char *last_name;
    char *phone_number;
    char *email_address;
} *client;

static void *emalloc(size_t s)
{
    void *ptr = malloc(s);

    if (NULL == ptr) {
        fprintf(stderr, "Fatal: memory allocation failed.\n");
        exit(EXIT_FAILURE);
    }

    return ptr;
}

static void print_help_message()
{
    printf("f <query> : searches the records by first name.\n");
    printf("l <query> : searches the records by last name.\n");
    printf("p <query> : searches the records by phone number.\n");
    printf("e <query> : searches the records by email address.\n");
    printf("h         : prints this message.\n");
    printf("q         : quits the program.\n\n");
}

static void print_client(client c)
{
    printf("First Name    : %s\n", c->first_name);
    printf("Last Name     : %s\n", c->last_name);
    printf("Phone Number  : %s\n", c->phone_number);
    printf("Email Address : %s\n\n", c->email_address);
}

static char *get_attribute(client c, attribute attr)
{
    switch (attr) {
        case FIRST_NAME:
            return c->first_name;
        case LAST_NAME:
            return c->last_name;
        case PHONE_NUMBER:
            return c->phone_number;
        case EMAIL_ADDRESS:
            return c->email_address;
    }

    return NULL;
}

static int binsearch(attribute attr, char *key, client *clients_list, int l, int r)
{
    int m;

    if (r >= l) {
        m = l + (r - l) / 2;

        if (strncasecmp(get_attribute(clients_list[m], attr), key, strlen(key)) == 0) return m;
        if (strncasecmp(get_attribute(clients_list[m], attr), key, strlen(key)) > 0) return binsearch(attr, key, clients_list, l, m - 1);

        return binsearch(attr, key, clients_list, m + 1, r);
    }

    return NO_MATCH;
}

static void search(client **matches_ptr, int *matches_size_ptr, attribute attr, char *key, client *clients_list, int clients_size)
{
    int match_idx, match_base, match_end;

    match_idx = binsearch(attr, key, clients_list, 0, clients_size - 1);
    if (match_idx == NO_MATCH) return;

    for (match_end = match_idx; match_end > 0 && strncasecmp(get_attribute(clients_list[match_end - 1], attr), key, strlen(key)) == 0; match_end--);
    match_base = match_end;
    for (; match_end < clients_size && strncasecmp(get_attribute(clients_list[match_end], attr), key, strlen(key)) == 0; match_end++);

    *matches_size_ptr = match_end - match_base;
    *matches_ptr = emalloc(*matches_size_ptr * sizeof (*matches_ptr)[0]);

    match_idx = 0;
    for (; match_base < match_end; match_base++) {
        (*matches_ptr)[match_idx++] = clients_list[match_base];
    }
}

static void merge(client *clients_list, int l, int m, int r, attribute attr)
{
    int     l_idx, r_idx, client_idx;
    int     l_size = m - l + 1, r_size = r - m;
    client  l_swap[l_size], r_swap[r_size];
    char   *l_str, *r_str;

    for (l_idx = 0; l_idx < l_size; l_idx++) {
        l_swap[l_idx] = clients_list[l + l_idx];
    }
    for (r_idx = 0; r_idx < r_size; r_idx++) {
        r_swap[r_idx] = clients_list[m + 1 + r_idx];
    }

    l_idx = r_idx = 0;
    client_idx = l;
    while (l_idx < l_size && r_idx < r_size) {
        l_str = get_attribute(l_swap[l_idx], attr);
        r_str = get_attribute(r_swap[r_idx], attr);
        if (strcasecmp(l_str, r_str) <= 0) {
            clients_list[client_idx++] = l_swap[l_idx++];
        } else {
            clients_list[client_idx++] = r_swap[r_idx++];
        }
    }

    while (l_idx < l_size) {
        clients_list[client_idx++] = l_swap[l_idx++];
    }
    while (r_idx < r_size) {
        clients_list[client_idx++] = r_swap[r_idx++];
    }
}

static void merge_sort(client *clients_list, int l, int r, attribute attr)
{
    int m;

    if (l < r) {
        m = l + (r - l) / 2;

        merge_sort(clients_list, l, m, attr);
        merge_sort(clients_list, m + 1, r, attr);

        merge(clients_list, l, m, r, attr);
    }
}

static void query(attribute attr, char *key, client *clients_list, int clients_size)
{
    client *matches = NULL;
    int     matches_size = 0, match_idx;

    if (NULL == key) {
        printf("Error: no search string was entered. Type \"h\" for help.\n\n");
        return;
    }

    merge_sort(clients_list, 0, clients_size - 1, attr);
    search(&matches, &matches_size, attr, key, clients_list, clients_size);

    printf("%d record%s%s\n\n", matches_size, matches_size == 1 ? "" : "s", matches_size == 0 ? "." : ":");
    for (match_idx = 0; match_idx < matches_size; match_idx++) {
        print_client(matches[match_idx]);
    }

    if (matches_size > 0) free(matches);
}

int main(int argc, char **argv)
{
    FILE      *clients_fp;
    char       client_str[MAX_CLIENT_LENGTH];
    char      *first_name_buf, *last_name_buf, *phone_number_buf, *email_address_buf;
    char       cmd_str[MAX_CMD_LEN], *cmd, *key;
    int        clients_size = 0, client_idx = 0;
    client    *clients_list, client_buf;
    quit_flag  q = RUN;

    if (argc != 2) {
        fprintf(stderr, "Fatal: invalid number of arguments: %d. This program takes 1.\n", argc - 1);
        return EXIT_FAILURE;
    }
    if (NULL == (clients_fp = fopen(argv[1], "r"))) {
        fprintf(stderr, "Fatal: could not read file \"%s\".\n", argv[1]);
        return EXIT_FAILURE;
    }

    while (NULL != fgets(client_str, MAX_CLIENT_LENGTH, clients_fp)) clients_size++;
    clients_list = emalloc(clients_size * sizeof clients_list[0]);

    rewind(clients_fp);
    while (NULL != fgets(client_str, MAX_CLIENT_LENGTH, clients_fp) && client_idx < clients_size) {
        if (client_str[strlen(client_str) - 1] == '\n') client_str[strlen(client_str) - 1] = '\0';

        first_name_buf = strtok(client_str, " ");
        last_name_buf = strtok(NULL, " ");
        phone_number_buf = strtok(NULL, " ");
        email_address_buf = strtok(NULL, " ");

        client_buf = emalloc(sizeof *client_buf);

        client_buf->first_name = emalloc((strlen(first_name_buf) + 1) * sizeof client_buf->first_name[0]);
        client_buf->last_name = emalloc((strlen(last_name_buf) + 1) * sizeof client_buf->last_name[0]);
        client_buf->phone_number = emalloc((strlen(phone_number_buf) + 1) * sizeof client_buf->phone_number[0]);
        client_buf->email_address = emalloc((strlen(email_address_buf) + 1) * sizeof client_buf->email_address[0]);

        strcpy(client_buf->first_name, first_name_buf);
        strcpy(client_buf->last_name, last_name_buf);
        strcpy(client_buf->phone_number, phone_number_buf);
        strcpy(client_buf->email_address, email_address_buf);

        clients_list[client_idx++] = client_buf;
    }
    fclose(clients_fp);

    printf("Welcome to Client Search. type \"h\" for help.\n\n");
    while (q == RUN) {
        printf(">>> ");

        fgets(cmd_str, MAX_CMD_LEN, stdin);
        if (cmd_str[strlen(cmd_str) - 1] == '\n') cmd_str[strlen(cmd_str) - 1] = '\0';
        if (strlen(cmd_str) == 0 || strlen(cmd_str) > MAX_CMD_LEN) {
            printf("Error: bad input length. Type \"h\" for help.\n");
            continue;
        }

        cmd = strtok(cmd_str, " ");
        if (strlen(cmd) != 1) {
            printf("Error: invalid command \"%s\". Type \"h\" for help.\n\n", cmd);
            continue;
        }

        key = strtok(NULL, " ");

        if (NULL != strtok(NULL, " ")) {
            printf("Error: too many command parameters. Type \"h\" for help.\n\n");
        }

        switch (cmd[0]) {
            case 'f':
                query(FIRST_NAME, key, clients_list, clients_size);
                break;
            case 'l':
                query(LAST_NAME, key, clients_list, clients_size);
                break;
            case 'p':
                query(PHONE_NUMBER, key, clients_list, clients_size);
                break;
            case 'e':
                query(EMAIL_ADDRESS, key, clients_list, clients_size);
                break;
            case 'h':
                print_help_message();
                break;
            case 'q':
                q = QUIT;
                break;
            default:
                printf("Error: invalid command \"%c\". Type \"h\" for help.\n\n", cmd[0]);
                break;
        }
    }

    for (client_idx = 0; client_idx < clients_size; client_idx++) {
        free(clients_list[client_idx]->first_name);
        free(clients_list[client_idx]->last_name);
        free(clients_list[client_idx]->phone_number);
        free(clients_list[client_idx]->email_address);

        free(clients_list[client_idx]);
    }
    free(clients_list);

    return EXIT_SUCCESS;
}
