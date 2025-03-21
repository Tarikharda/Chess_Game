
int base64_max_encoded_len(int bytes_size);

int base64_max_decoded_len(int str_len);

char* base64_encode(const char* bytes, int bytes_len, int is_url, char *out_chars, int out_chars_maxlen, int* out_len);

char* base64_decode(const char* base64, char *out_buff, int out_buff_maxlen, int* out_len);