## Nhóm Thực thi lệnh (Execution Methods)

Đây là các hàm quyết định cách bot sẽ gửi request lên Discord. Bắt buộc phải gọi một trong các hàm này (hoặc nhóm Hẹn giờ bên dưới) thì lệnh mới thực sự chạy.

| Phương thức                                                  | Tác dụng                                                                                                               | Chú thích                                                                                                               | 
|--------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| `queue()`<br/>`queue(success)`<br/>`queue(success, failure)` | Đẩy request vào hàng đợi và chạy ngầm (Bất đồng bộ - Asynchronous). Không làm đứng code của bạn.                       | **Được khuyên dùng nhất.** `success` chạy khi thành công, `failure` chạy khi có lỗi.                                    |
| `complete()`<br/>`complete(shouldQueue) `                    | Chạy đồng bộ (Synchronous). Bắt luồng (thread) hiện tại phải đứng chờ cho đến khi Discord trả về kết quả hoặc báo lỗi. | Dễ dùng cho logic tuần tự, nhưng **dễ gây kẹt bot** nếu lạm dụng. Không được dùng bên trong callback của hàm `queue()`. |                                                                                                                    |
| `submit()`<br/>`submit(shouldQueue)`                         | Gửi request và trả về một `CompletableFuture<T>`.                                                                      | Dành cho các bạn quen dùng API bất đồng bộ nâng cao của Java. Rất tiện để nối chuỗi (chain) nhiều tác vụ phức tạp.      |
---
## Nhóm Hẹn giờ & Trì hoãn (Scheduling & Delaying)

Cho phép bạn lên lịch thực thi lệnh sau một khoảng thời gian nhất định.

| Phương thức          | Tác dụng                                                                                                                                                                     |
|----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `queueAfter(...)`    | Hẹn giờ gọi hàm `queue()`. (Có nhiều phiên bản để bạn truyền thêm callback thành công/thất bại hoặc chọn ThreadPool riêng).                                                  |
| `completeAfter(...)` | Làm bot ""ngủ"" (block thread) trong một khoảng thời gian, sau đó gọi `complete()`.                                                                                          |
| `submitAfter(...)`   | Hẹn giờ trả về `CompletableFuture` để xử lý logic phức tạp.                                                                                                                  |
| `delay(...)`         | Trả về một RestAction mới, thêm thời gian chờ **sau khi** tác vụ hoàn thành. Thường kết hợp với các hàm nối chuỗi bên dưới (Vd: Gửi tin nhắn -> `delay` 5s -> xóa tin nhắn). |
---
## Nhóm Nối chuỗi & Biến đổi dữ liệu (Chaining & Mapping Operators)

Lấy cảm hứng từ Java Stream API, các hàm này giúp bạn xử lý luồng dữ liệu gọn gàng mà không bị "Callback Hell" (lồng nhau quá nhiều).

| Phương thức                                         | Tác dụng                                                                                                                                               |
|-----------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| `map(Function)`                                     | Lấy kết quả từ RestAction này biến đổi thành kiểu khác. (VD: Lấy `Member` -> `map` thành `String` chứa Tên).                                           |
| `flatMap(Function)`                                 | Dùng kết quả của RestAction hiện tại để tạo ra một **RestAction mới** nối tiếp theo. (VD: Lấy `Channel` -> `flatMap` để gửi `Message` vào channel đó). |
| `onSuccess(Consumer)`                               | Làm một việc gì đó với kết quả trả về, nhưng vẫn giữ nguyên giá trị để truyền cho tác vụ tiếp theo (VD: log ra console)."                              |
| `and(RestAction)`<br/>`and(RestAction, BiFunction)` | Chạy song song/kết hợp RestAction hiện tại với một RestAction khác. Gộp kết quả của cả hai lại.                                                        |
| `zip(RestAction...)`                                | Gộp kết quả của RestAction hiện tại và nhiều RestAction khác vào một danh sách (`List<T>`).                                                            |
---
## Nhóm Xử lý lỗi (Error Handling)

Giúp bot của bạn không bị "chết" hoặc quăng Exception khi gặp lỗi (ví dụ: gửi tin cho người đã block bot).

| Phương thức           | Tác dụng                                                                                                                                                            |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `onErrorMap(...)`     | Bắt lỗi và cung cấp một **giá trị thay thế/mặc định** thay vì ném ra Exception.                                                                                     |
| `onErrorFlatMap(...)` | Bắt lỗi và chuyển hướng thực thi sang một **RestAction chữa cháy** khác (VD: Lỗi gửi tin nhắn riêng -> gửi tin nhắn vào kênh chung để báo lỗi).                     |
| `mapToResult()`       | "Gói kết quả thành công và lỗi vào trong một object **Result**. Rất hữu ích khi bạn chạy hàng loạt tác vụ và muốn tự tay kiểm tra từng cái nào fail, cái nào pass." |
---
## Nhóm Kiểm tra & Giới hạn (Checks & Limits)

| Phương thức                 | Tác dụng                                                                                                                               |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| `setCheck(BooleanSupplier)` | Đặt điều kiện. Ngay trước khi gửi request lên Discord, nếu điều kiện này trả về `false`, lệnh sẽ bị hủy.                               |
| `addCheck(BooleanSupplier)` | Thêm điều kiện mới (ghép nối bằng toán tử AND với điều kiện cũ).                                                                       |
| `getCheck()`                | Lấy ra hàm điều kiện đang được áp dụng.                                                                                                |
| `timeout(long, TimeUnit)`   | Đặt thời gian tối đa để request chờ trong hàng đợi. Nếu quá thời gian chưa được xử lý, request tự động hủy (bắn lỗi TimeoutException). |
| `deadline(long)`            | Đặt một mốc thời gian cụ thể (timestamp). Nếu mốc này trôi qua mà request chưa chạy xong thì sẽ hủy bỏ.                                |
---
## Nhóm Hàm Tiện ích & Cấu hình Toàn cục (Static Configuration)

Đây là các hàm static dùng để cấu hình chung cho toàn bộ các RestAction trong project.

| Phương thức                                     | Tác dụng                                                                                                                                                           |
|-------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `allOf(RestAction...)`<br/>`allOf(Collection)`  | Chạy đồng loạt nhiều RestAction và gom tất cả kết quả lại thành một `RestAction<List<E>>`. Rất mạnh mẽ để làm các lệnh bulk (xử lý hàng loạt).                     |
| `accumulate(...)`                               | Giống `allOf` nhưng cho phép bạn tự định nghĩa cách gom dữ liệu bằng `Collector`.                                                                                  |
| `setDefaultSuccess()`<br/>`setDefaultFailure()` | Đặt hành vi mặc định khi bạn gọi `queue()` mà không truyền callback. (Ví dụ: đặt default failure là in lỗi ra console).                                            |
| `getDefaultSuccess()`<br/>`getDefaultFailure()` | Lấy ra các callback mặc định đang được sử dụng.                                                                                                                    |
| `setDefaultTimeout()`<br/>`getDefaultTimeout()` | Cài đặt/Lấy thời gian timeout mặc định cho mọi RestAction.                                                                                                         |
| `setPassContext()`<br/>`isPassContext()`        | "Bật/tắt tính năng lưu vết (stack trace) gốc khi chạy bất đồng bộ. Bật lên giúp dễ debug lỗi xem nó xuất phát từ dòng nào, nhưng đánh đổi bằng hiệu năng của bot." |
| `getJDA()`                                      | Trả về instance của JDA đang quản lý RestAction này.                                                                                                               |

