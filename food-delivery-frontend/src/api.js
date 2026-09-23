// ==============================================================================
// Food Delivery API Service Layer (Group B02 Platform)
// Dynamic endpoint resolution, raw request execution, and full endpoint catalog
// ==============================================================================

const STORAGE_KEY_API_HOST = 'food_delivery_api_host'

export const resolveApiBase = () => {
  // 1. Check user custom host from localStorage
  if (typeof window !== 'undefined') {
    const savedHost = localStorage.getItem(STORAGE_KEY_API_HOST)
    if (savedHost && savedHost.trim().length > 0) {
      return savedHost.trim().replace(/\/+$/, '')
    }
  }

  // 2. Check Vite env variable
  const envUrl = import.meta.env.VITE_API_BASE_URL
  if (envUrl && !envUrl.includes('localhost') && !envUrl.includes('127.0.0.1')) {
    try {
      const u = new URL(envUrl)
      return `${u.protocol}//${u.host}`
    } catch {
      return 'http://194.242.57.93:8082'
    }
  }

  // 3. Fallback to current host or default backend port
  const hostname = typeof window !== 'undefined' && window.location.hostname ? window.location.hostname : 'localhost'
  return `http://${hostname}:8082`
}

let activeApiHost = resolveApiBase()

export const getApiHost = () => activeApiHost

export const setApiHost = (newHost) => {
  if (!newHost || typeof newHost !== 'string') return
  const cleaned = newHost.trim().replace(/\/+$/, '')
  activeApiHost = cleaned
  if (typeof window !== 'undefined') {
    localStorage.setItem(STORAGE_KEY_API_KEY_HOST || STORAGE_KEY_API_HOST, cleaned)
  }
  return activeApiHost
}

export const resetApiHost = () => {
  if (typeof window !== 'undefined') {
    localStorage.removeItem(STORAGE_KEY_API_HOST)
  }
  activeApiHost = resolveApiBase()
  return activeApiHost
}

// ------------------------------------------------------------------------------
// Standard Request Helper
// ------------------------------------------------------------------------------
export const request = async (path, options = {}) => {
  const url = `${activeApiHost}${path.startsWith('/') ? path : `/${path}`}`
  const defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json, text/plain, */*'
  }

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers
    }
  }

  if (config.body && typeof config.body === 'object' && !(config.body instanceof FormData)) {
    config.body = JSON.stringify(config.body)
  }

  try {
    const response = await fetch(url, config)
    const text = await response.text()

    let data
    try {
      data = JSON.parse(text)
    } catch {
      data = text
    }

    if (!response.ok) {
      const errorMsg = (data && data.message) || (data && data.error) || (typeof data === 'string' && data) || `HTTP ${response.status}: ${response.statusText}`
      throw new Error(errorMsg)
    }

    return data
  } catch (err) {
    console.error(`API Error on ${options.method || 'GET'} ${path}:`, err)
    throw err
  }
}

// ------------------------------------------------------------------------------
// Raw Request Runner for API Tester Workbench
// Measures latency, extracts HTTP status code, response headers, and raw body
// ------------------------------------------------------------------------------
export const executeRawRequest = async ({ method = 'GET', path = '', queryParams = {}, headers = {}, body = null }) => {
  let url = `${activeApiHost}${path.startsWith('/') ? path : `/${path}`}`
  
  // Append query params if any
  const queryEntries = Object.entries(queryParams).filter(([_, v]) => v !== undefined && v !== null && v !== '')
  if (queryEntries.length > 0) {
    const qs = new URLSearchParams()
    queryEntries.forEach(([k, v]) => qs.append(k, String(v)))
    url += `${url.includes('?') ? '&' : '?'}${qs.toString()}`
  }

  const finalHeaders = {
    'Accept': 'application/json, text/plain, */*',
    ...headers
  }

  const config = {
    method: method.toUpperCase(),
    headers: finalHeaders
  }

  if (body !== null && body !== undefined && ['POST', 'PUT', 'PATCH', 'DELETE'].includes(config.method)) {
    if (typeof body === 'object') {
      config.headers['Content-Type'] = config.headers['Content-Type'] || 'application/json'
      config.body = JSON.stringify(body)
    } else {
      config.body = String(body)
    }
  }

  const startTime = performance.now()
  try {
    const response = await fetch(url, config)
    const endTime = performance.now()
    const durationMs = Math.round(endTime - startTime)
    
    const text = await response.text()
    let parsedData
    try {
      parsedData = JSON.parse(text)
    } catch {
      parsedData = text
    }

    const responseHeaders = {}
    response.headers.forEach((val, key) => {
      responseHeaders[key] = val
    })

    return {
      success: response.ok,
      status: response.status,
      statusText: response.statusText,
      durationMs,
      headers: responseHeaders,
      data: parsedData,
      rawBody: text,
      url,
      method: config.method
    }
  } catch (err) {
    const endTime = performance.now()
    return {
      success: false,
      status: 0,
      statusText: 'Network / Connection Error',
      durationMs: Math.round(endTime - startTime),
      headers: {},
      data: { error: err.message, stack: err.stack },
      rawBody: err.message,
      url,
      method: config.method
    }
  }
}

// ------------------------------------------------------------------------------
// 1. Health & Observability (Actuator)
// ------------------------------------------------------------------------------
export const getHealth = () => request('/actuator/health')
export const getInfo = () => request('/actuator/info')
export const getPrometheusMetrics = () => request('/actuator/prometheus')

// ------------------------------------------------------------------------------
// 2. Users Management (/api/users)
// ------------------------------------------------------------------------------
export const getAllUsers = () => request('/api/users')
export const getUserById = (id) => request(`/api/users/${id}`)
export const createUser = (user) => request('/api/users', { method: 'POST', body: user })
export const updateUser = (id, user) => request(`/api/users/${id}`, { method: 'PUT', body: user })
export const deleteUser = (id) => request(`/api/users/${id}`, { method: 'DELETE' })
export const deleteAllUsers = () => request('/api/users', { method: 'DELETE' })

// ------------------------------------------------------------------------------
// 3. User Addresses (/api/user-address)
// ------------------------------------------------------------------------------
export const saveUserAddress = (addr) => request('/api/user-address', { method: 'POST', body: addr })
export const getUserAddressById = (id) => request(`/api/user-address/${id}`)
export const getUserAddresses = (userId) => request(`/api/user-address/user/${userId}`)
export const getUserAddressByType = (type, userId) => request(`/api/user-address/user/${type}/${userId}`)
export const updateUserAddress = (addressId, addr) => request(`/api/user-address/${addressId}`, { method: 'PUT', body: addr })
export const deleteUserAddress = (id) => request(`/api/user-address/${id}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// 4. Roles Management (/api/roles)
// ------------------------------------------------------------------------------
export const getAllRoles = () => request('/api/roles')
export const getRoleById = (id) => request(`/api/roles/${id}`)
export const createRole = (role) => request('/api/roles', { method: 'POST', body: role })
export const deleteRole = (id) => request(`/api/roles/${id}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// 5. Restaurants (/api/restaurants)
// ------------------------------------------------------------------------------
export const getAllRestaurants = () => request('/api/restaurants')
export const getRestaurantById = (id) => request(`/api/restaurants/${id}`)
export const createRestaurant = (rest) => request('/api/restaurants', { method: 'POST', body: rest })
export const updateRestaurant = (id, rest) => request(`/api/restaurants/${id}`, { method: 'PUT', body: rest })
export const searchRestaurantsByCity = (city) => request(`/api/restaurants/search?city=${encodeURIComponent(city)}`)
export const deleteRestaurant = (id) => request(`/api/restaurants/${id}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// 6. Restaurant Addresses (/api/restaurant-addresses)
// ------------------------------------------------------------------------------
export const getAllRestaurantAddresses = () => request('/api/restaurant-addresses')
export const getRestaurantAddressById = (id) => request(`/api/restaurant-addresses/${id}`)
export const saveRestaurantAddress = (addr) => request('/api/restaurant-addresses', { method: 'POST', body: addr })
export const updateRestaurantAddress = (id, addr) => request(`/api/restaurant-addresses/${id}`, { method: 'PUT', body: addr })
export const deleteRestaurantAddress = (id) => request(`/api/restaurant-addresses/${id}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// 7. Food Items (Menu) (/food)
// ------------------------------------------------------------------------------
export const getAllFoodItems = () => request('/food/all')
export const getFoodItemById = (id) => request(`/food/${id}`)
export const createFoodItem = (item) => request('/food/add', { method: 'POST', body: item })
export const updateFoodItem = (id, item) => request(`/food/update/${id}`, { method: 'PUT', body: item })
export const deleteFoodItem = (id) => request(`/food/delete/${id}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// 8. Item Pricing (/{itemId})
// ------------------------------------------------------------------------------
export const getItemPrice = (itemId) => request(`/${itemId}`)

// ------------------------------------------------------------------------------
// 9. Cart & Cart Items (/api/cart & /api/cart/items)
// ------------------------------------------------------------------------------
export const getCartByUserId = (userId) => request(`/api/cart/${userId}`)
export const getAllCarts = () => request('/api/cart')
export const createCart = (userId, restaurantId) => 
  request('/api/cart', { method: 'POST', body: { userId, restaurentId: restaurantId } })
export const deleteCart = (cartId) => request(`/api/cart/${cartId}`, { method: 'DELETE' })

export const getAllCartItems = () => request('/api/cart/items')
export const getCartItemById = (id) => request(`/api/cart/items/${id}`)
export const addCartItem = (cartId, foodItemId, quantity = 1) => 
  request('/api/cart/items', { method: 'POST', body: { cartId, foodItemId, quantity } })
export const deleteCartItem = (cartItemId) => request(`/api/cart/items/${cartItemId}`, { method: 'DELETE' })
export const deleteAllCartItems = () => request('/api/cart/items', { method: 'DELETE' })

// ------------------------------------------------------------------------------
// 10. Delivery Fee & Pricing Rules (/delieverypricing & /api/prices)
// ------------------------------------------------------------------------------
export const getAllDeliveryPricingRules = () => request('/delieverypricing')
export const saveDeliveryPricingRule = (rule) => request('/delieverypricing/add', { method: 'POST', body: rule })
export const calculateDeliveryFee = (restaurantAddressId, userAddressId, cartId) =>
  request('/api/prices/delivery-fee', {
    method: 'POST',
    body: { restaurantAddressId, userAddressId, cartId }
  })

// ------------------------------------------------------------------------------
// 11. Orders (/api/orders)
// ------------------------------------------------------------------------------
export const getAllOrders = () => request('/api/orders')
export const getOrderById = (id) => request(`/api/orders/${id}`)
export const createOrder = (orderData) => request('/api/orders', { method: 'POST', body: orderData })
export const updateOrderStatus = (id, status) => request(`/api/orders/${id}?status=${encodeURIComponent(status)}`, { method: 'PUT' })
export const cancelOrder = (id) => request(`/api/orders/${id}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// 12. Payments & Stripe Gateway (/api/payments & /api/webhooks/stripe)
// ------------------------------------------------------------------------------
export const getAllPayments = () => request('/api/payments')
export const getPaymentById = (id) => request(`/api/payments/${id}`)
export const initiatePayment = (paymentData) => request('/api/payments/initiate', { method: 'POST', body: paymentData })
export const createPayment = (paymentData) => request('/api/payments', { method: 'POST', body: paymentData })
export const updatePaymentStatus = (id, status) => request(`/api/payments/${id}/status?status=${encodeURIComponent(status)}`, { method: 'PUT' })
export const deletePayment = (id) => request(`/api/payments/${id}`, { method: 'DELETE' })
export const simulateStripeWebhook = (payload, sigHeader = '') => 
  request('/api/webhooks/stripe', {
    method: 'POST',
    headers: sigHeader ? { 'Stripe-Signature': sigHeader } : {},
    body: typeof payload === 'string' ? payload : JSON.stringify(payload)
  })

// ------------------------------------------------------------------------------
// 13. Feedback & Reviews (/api/feedback)
// ------------------------------------------------------------------------------
export const getAllFeedback = () => request('/api/feedback')
export const getFeedbackById = (id) => request(`/api/feedback/${id}`)
export const getRestaurantFeedback = (restaurantId) => request(`/api/feedback/restaurant/${restaurantId}`)
export const createFeedback = (feedback) => request('/api/feedback', { method: 'POST', body: feedback })
export const updateFeedback = (id, feedback) => request(`/api/feedback/${id}`, { method: 'PUT', body: feedback })
export const deleteFeedback = (id) => request(`/api/feedback/${id}`, { method: 'DELETE' })

// ==============================================================================
// Comprehensive API Catalog for Interactive Tester Workbench
// ==============================================================================
export const API_CATALOG = [
  // 1. Observability
  {
    id: 'actuator-health',
    category: 'Observability & Health',
    name: 'Service Health Check',
    method: 'GET',
    path: '/actuator/health',
    description: 'Checks application status, database connectivity, and mail services.',
    sampleBody: null
  },
  {
    id: 'actuator-info',
    category: 'Observability & Health',
    name: 'Service Info',
    method: 'GET',
    path: '/actuator/info',
    description: 'Returns application metadata and build details.',
    sampleBody: null
  },
  {
    id: 'actuator-prometheus',
    category: 'Observability & Health',
    name: 'Prometheus Metrics',
    method: 'GET',
    path: '/actuator/prometheus',
    description: 'Scrapes Prometheus performance metrics for monitoring.',
    sampleBody: null
  },

  // 2. Users
  {
    id: 'users-create',
    category: 'Users Management',
    name: 'Create User (Register)',
    method: 'POST',
    path: '/api/users',
    description: 'Creates a new user. Role defaults to CUSTOMER (roleId: 1). Email must end with @gmail.com.',
    sampleBody: {
      name: "Rahul Verma",
      email: "rahul.verma88@gmail.com",
      mobile: "9876543210",
      roleId: 1,
      password: "SecurePass@123"
    }
  },
  {
    id: 'users-get-all',
    category: 'Users Management',
    name: 'Get All Users',
    method: 'GET',
    path: '/api/users',
    description: 'Fetches all registered users in the database.',
    sampleBody: null
  },
  {
    id: 'users-get-by-id',
    category: 'Users Management',
    name: 'Get User by ID',
    method: 'GET',
    path: '/api/users/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'User ID' }],
    description: 'Fetches a single user with associated addresses.',
    sampleBody: null
  },
  {
    id: 'users-update',
    category: 'Users Management',
    name: 'Update User',
    method: 'PUT',
    path: '/api/users/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'User ID' }],
    description: 'Updates details of an existing user.',
    sampleBody: {
      name: "Rahul Verma Updated",
      email: "rahul.verma88@gmail.com",
      mobile: "9876543210",
      password: "UpdatedPassword@123"
    }
  },
  {
    id: 'users-delete',
    category: 'Users Management',
    name: 'Delete User by ID',
    method: 'DELETE',
    path: '/api/users/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'User ID' }],
    description: 'Deletes a user account by ID.',
    sampleBody: null
  },
  {
    id: 'users-delete-all',
    category: 'Users Management',
    name: 'Delete All Users',
    method: 'DELETE',
    path: '/api/users',
    description: 'Deletes all users in the system (testing only).',
    sampleBody: null
  },

  // 3. User Addresses
  {
    id: 'addr-create',
    category: 'User Addresses',
    name: 'Save User Address',
    method: 'POST',
    path: '/api/user-address',
    description: 'Saves a delivery address linked to a user.',
    sampleBody: {
      userId: 1,
      houseNo: "Flat 402",
      buildingName: "Sai Residency",
      street: "FC Road",
      landmark: "Opposite Starbucks",
      area: "Deccan Gymkhana",
      city: "Pune",
      state: "Maharashtra",
      pincode: 411004,
      addressType: "HOME",
      latitude: 18.5204,
      longitude: 73.8567
    }
  },
  {
    id: 'addr-get-by-id',
    category: 'User Addresses',
    name: 'Get Address by ID',
    method: 'GET',
    path: '/api/user-address/{addressId}',
    pathParams: [{ key: 'addressId', default: '1', label: 'Address ID' }],
    description: 'Fetches address record by its unique address ID.',
    sampleBody: null
  },
  {
    id: 'addr-get-by-user',
    category: 'User Addresses',
    name: 'Get All Addresses by User ID',
    method: 'GET',
    path: '/api/user-address/user/{userId}',
    pathParams: [{ key: 'userId', default: '1', label: 'User ID' }],
    description: 'Fetches all delivery addresses saved by a given user.',
    sampleBody: null
  },
  {
    id: 'addr-get-by-type-user',
    category: 'User Addresses',
    name: 'Get Address by Type & User',
    method: 'GET',
    path: '/api/user-address/user/{type}/{userId}',
    pathParams: [
      { key: 'type', default: 'HOME', label: 'Address Type (HOME/WORK/OTHER)' },
      { key: 'userId', default: '1', label: 'User ID' }
    ],
    description: 'Fetches user address filtered by specific type tag.',
    sampleBody: null
  },
  {
    id: 'addr-update',
    category: 'User Addresses',
    name: 'Update Address',
    method: 'PUT',
    path: '/api/user-address/{addressId}',
    pathParams: [{ key: 'addressId', default: '1', label: 'Address ID' }],
    description: 'Updates details of an existing user address.',
    sampleBody: {
      userId: 1,
      houseNo: "Flat 402-B",
      buildingName: "Sai Residency Phase 2",
      street: "FC Road Extension",
      landmark: "Near Metro Gate 1",
      area: "Deccan Gymkhana",
      city: "Pune",
      state: "Maharashtra",
      pincode: 411004,
      addressType: "HOME",
      latitude: 18.5210,
      longitude: 73.8570
    }
  },
  {
    id: 'addr-delete',
    category: 'User Addresses',
    name: 'Delete Address by ID',
    method: 'DELETE',
    path: '/api/user-address/{addressId}',
    pathParams: [{ key: 'addressId', default: '1', label: 'Address ID' }],
    description: 'Deletes an address record.',
    sampleBody: null
  },

  // 4. Roles
  {
    id: 'roles-create',
    category: 'Roles Management',
    name: 'Create Role',
    method: 'POST',
    path: '/api/roles',
    description: 'Adds a new system security role.',
    sampleBody: {
      roleId: 2,
      roleName: "RESTAURANT_ADMIN",
      roleDescription: "Manages menu, orders, and restaurant profile"
    }
  },
  {
    id: 'roles-get-all',
    category: 'Roles Management',
    name: 'Get All Roles',
    method: 'GET',
    path: '/api/roles',
    description: 'Fetches all registered system roles.',
    sampleBody: null
  },
  {
    id: 'roles-get-by-id',
    category: 'Roles Management',
    name: 'Get Role by ID',
    method: 'GET',
    path: '/api/roles/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Role ID' }],
    description: 'Fetches a single role by ID.',
    sampleBody: null
  },
  {
    id: 'roles-delete',
    category: 'Roles Management',
    name: 'Delete Role by ID',
    method: 'DELETE',
    path: '/api/roles/{id}',
    pathParams: [{ key: 'id', default: '2', label: 'Role ID' }],
    description: 'Deletes a role by ID.',
    sampleBody: null
  },

  // 5. Restaurants
  {
    id: 'restaurants-create',
    category: 'Restaurants Management',
    name: 'Create Restaurant',
    method: 'POST',
    path: '/api/restaurants',
    description: 'Registers a partner restaurant.',
    sampleBody: {
      name: "Spice Symphony Bistro",
      phone: "9123456780",
      email: "spice.bistro@gmail.com",
      description: "Fine dining North Indian, Tandoor & Mughlai specialties.",
      open: true
    }
  },
  {
    id: 'restaurants-get-all',
    category: 'Restaurants Management',
    name: 'Get All Restaurants',
    method: 'GET',
    path: '/api/restaurants',
    description: 'Fetches all partner restaurants.',
    sampleBody: null
  },
  {
    id: 'restaurants-get-by-id',
    category: 'Restaurants Management',
    name: 'Get Restaurant by ID',
    method: 'GET',
    path: '/api/restaurants/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Restaurant ID' }],
    description: 'Fetches single restaurant details.',
    sampleBody: null
  },
  {
    id: 'restaurants-search-city',
    category: 'Restaurants Management',
    name: 'Search Restaurants by City',
    method: 'GET',
    path: '/api/restaurants/search',
    queryParams: [{ key: 'city', default: 'Pune', label: 'City Name' }],
    description: 'Finds restaurants located in a target city.',
    sampleBody: null
  },
  {
    id: 'restaurants-update',
    category: 'Restaurants Management',
    name: 'Update Restaurant',
    method: 'PUT',
    path: '/api/restaurants/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Restaurant ID' }],
    description: 'Updates restaurant name, status, or description.',
    sampleBody: {
      name: "Spice Symphony Bistro & Grill",
      phone: "9123456780",
      email: "spice.bistro@gmail.com",
      description: "Gourmet North Indian, Charcoal Kebabs & Desserts.",
      open: true
    }
  },
  {
    id: 'restaurants-delete',
    category: 'Restaurants Management',
    name: 'Delete Restaurant by ID',
    method: 'DELETE',
    path: '/api/restaurants/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Restaurant ID' }],
    description: 'Deletes a restaurant record.',
    sampleBody: null
  },

  // 6. Restaurant Addresses
  {
    id: 'rest-addr-create',
    category: 'Restaurant Addresses',
    name: 'Save Restaurant Address',
    method: 'POST',
    path: '/api/restaurant-addresses',
    description: 'Saves physical dispatch location for a restaurant.',
    sampleBody: {
      restaurantId: 1,
      shopNo: 12,
      street: "JM Road",
      area: "Shivaji Nagar",
      city: "Pune",
      state: "Maharashtra",
      pincode: "411005",
      latitude: 18.5308,
      longitude: 73.8475
    }
  },
  {
    id: 'rest-addr-get-all',
    category: 'Restaurant Addresses',
    name: 'Get All Restaurant Addresses',
    method: 'GET',
    path: '/api/restaurant-addresses',
    description: 'Fetches all registered restaurant dispatch centers.',
    sampleBody: null
  },
  {
    id: 'rest-addr-get-by-id',
    category: 'Restaurant Addresses',
    name: 'Get Restaurant Address by ID',
    method: 'GET',
    path: '/api/restaurant-addresses/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Address ID' }],
    description: 'Fetches a single restaurant address.',
    sampleBody: null
  },
  {
    id: 'rest-addr-update',
    category: 'Restaurant Addresses',
    name: 'Update Restaurant Address',
    method: 'PUT',
    path: '/api/restaurant-addresses/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Address ID' }],
    description: 'Updates a restaurant address.',
    sampleBody: {
      restaurantId: 1,
      shopNo: 14,
      street: "JM Road Premium Complex",
      area: "Shivaji Nagar",
      city: "Pune",
      state: "Maharashtra",
      pincode: "411005",
      latitude: 18.5310,
      longitude: 73.8480
    }
  },
  {
    id: 'rest-addr-delete',
    category: 'Restaurant Addresses',
    name: 'Delete Restaurant Address by ID',
    method: 'DELETE',
    path: '/api/restaurant-addresses/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Address ID' }],
    description: 'Deletes a restaurant address record.',
    sampleBody: null
  },

  // 7. Food Items (Menu)
  {
    id: 'food-create',
    category: 'Food Items (Menu)',
    name: 'Add Food Item to Menu',
    method: 'POST',
    path: '/food/add',
    description: 'Creates a menu dish linked to a restaurant.',
    sampleBody: {
      foodname: "Butter Chicken Masala",
      foodtype: "NON_VEG",
      description: "Tender chicken cooked in rich makhani gravy with aromatic butter and cream.",
      cuisine: "North Indian",
      price: 340.0,
      available: true,
      restaurantId: 1
    }
  },
  {
    id: 'food-get-all',
    category: 'Food Items (Menu)',
    name: 'Get All Food Items',
    method: 'GET',
    path: '/food/all',
    description: 'Fetches full menu catalog across all restaurants.',
    sampleBody: null
  },
  {
    id: 'food-get-by-id',
    category: 'Food Items (Menu)',
    name: 'Get Food Item by ID',
    method: 'GET',
    path: '/food/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Food Item ID' }],
    description: 'Fetches single food item details.',
    sampleBody: null
  },
  {
    id: 'food-update',
    category: 'Food Items (Menu)',
    name: 'Update Food Item',
    method: 'PUT',
    path: '/food/update/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Food Item ID' }],
    description: 'Updates dish pricing, availability, or details.',
    sampleBody: {
      foodname: "Butter Chicken Masala Special",
      foodtype: "NON_VEG",
      description: "Signature tender chicken in slow-simmered rich creamy makhani gravy.",
      cuisine: "North Indian",
      price: 360.0,
      available: true,
      restaurantId: 1
    }
  },
  {
    id: 'food-delete',
    category: 'Food Items (Menu)',
    name: 'Delete Food Item by ID',
    method: 'DELETE',
    path: '/food/delete/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Food Item ID' }],
    description: 'Removes a dish from the menu.',
    sampleBody: null
  },

  // 8. Item Pricing
  {
    id: 'price-get-item',
    category: 'Food Item Pricing',
    name: 'Get Item Price by Item ID',
    method: 'GET',
    path: '/{itemId}',
    pathParams: [{ key: 'itemId', default: '1', label: 'Food Item ID' }],
    description: 'Queries price information for a given item ID.',
    sampleBody: null
  },

  // 9. Cart & Cart Items
  {
    id: 'cart-create',
    category: 'Cart & Cart Items',
    name: 'Create / Initialize Cart',
    method: 'POST',
    path: '/api/cart',
    description: 'Initializes a new shopping cart for a user and restaurant.',
    sampleBody: {
      userId: 1,
      restaurentId: 1
    }
  },
  {
    id: 'cart-get-by-user',
    category: 'Cart & Cart Items',
    name: 'Get Cart by User ID',
    method: 'GET',
    path: '/api/cart/{userId}',
    pathParams: [{ key: 'userId', default: '1', label: 'User ID' }],
    description: 'Fetches active cart and line items for a user.',
    sampleBody: null
  },
  {
    id: 'cart-get-all',
    category: 'Cart & Cart Items',
    name: 'Get All Carts',
    method: 'GET',
    path: '/api/cart',
    description: 'Lists all carts in the system.',
    sampleBody: null
  },
  {
    id: 'cart-delete',
    category: 'Cart & Cart Items',
    name: 'Delete Cart by ID',
    method: 'DELETE',
    path: '/api/cart/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Cart ID' }],
    description: 'Deletes a cart and clears its contents.',
    sampleBody: null
  },
  {
    id: 'cart-items-add',
    category: 'Cart & Cart Items',
    name: 'Add Item to Cart',
    method: 'POST',
    path: '/api/cart/items',
    description: 'Adds or increments a dish quantity in the cart.',
    sampleBody: {
      cartId: 1,
      foodItemId: 1,
      quantity: 2
    }
  },
  {
    id: 'cart-items-get-all',
    category: 'Cart & Cart Items',
    name: 'Get All Cart Items',
    method: 'GET',
    path: '/api/cart/items',
    description: 'Lists all line items currently across all carts.',
    sampleBody: null
  },
  {
    id: 'cart-items-get-by-id',
    category: 'Cart & Cart Items',
    name: 'Get Cart Item by ID',
    method: 'GET',
    path: '/api/cart/items/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Cart Item ID' }],
    description: 'Fetches single cart item record.',
    sampleBody: null
  },
  {
    id: 'cart-items-delete-by-id',
    category: 'Cart & Cart Items',
    name: 'Delete Cart Item by ID',
    method: 'DELETE',
    path: '/api/cart/items/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Cart Item ID' }],
    description: 'Deletes a specific item from a cart.',
    sampleBody: null
  },
  {
    id: 'cart-items-delete-all',
    category: 'Cart & Cart Items',
    name: 'Delete All Cart Items',
    method: 'DELETE',
    path: '/api/cart/items',
    description: 'Clears all cart items across all carts.',
    sampleBody: null
  },

  // 10. Delivery Fee & Rules
  {
    id: 'pricing-rule-add',
    category: 'Delivery Pricing & Fees',
    name: 'Add Delivery Pricing Rule',
    method: 'POST',
    path: '/delieverypricing/add',
    description: 'Creates a distance-based delivery fee configuration.',
    sampleBody: {
      basefees: 30.0,
      perKmRate: 10.0,
      maxdelieveryradius: 15.0,
      freeDelievery: 500.0,
      active: true
    }
  },
  {
    id: 'pricing-rules-get-all',
    category: 'Delivery Pricing & Fees',
    name: 'Get All Delivery Pricing Rules',
    method: 'GET',
    path: '/delieverypricing',
    description: 'Fetches all active and historical pricing rules.',
    sampleBody: null
  },
  {
    id: 'pricing-calculate-fee',
    category: 'Delivery Pricing & Fees',
    name: 'Calculate Delivery Fee & Distance',
    method: 'POST',
    path: '/api/prices/delivery-fee',
    description: 'Calculates Haversine distance and dynamic delivery fee based on coordinates and cart value.',
    sampleBody: {
      restaurantAddressId: 1,
      userAddressId: 1,
      cartId: 1
    }
  },

  // 11. Orders
  {
    id: 'orders-create',
    category: 'Orders Management',
    name: 'Create Order (Checkout)',
    method: 'POST',
    path: '/api/orders',
    description: 'Places a new food order from active cart contents.',
    sampleBody: {
      userId: 1,
      restaurantId: 1,
      deliveryAddressId: 1,
      paymentMethod: "STRIPE",
      couponCode: "WELCOME50",
      items: [
        {
          foodItemId: 1,
          quantity: 2
        }
      ]
    }
  },
  {
    id: 'orders-get-all',
    category: 'Orders Management',
    name: 'Get All Orders',
    method: 'GET',
    path: '/api/orders',
    description: 'Fetches all orders in the system.',
    sampleBody: null
  },
  {
    id: 'orders-get-by-id',
    category: 'Orders Management',
    name: 'Get Order by ID',
    method: 'GET',
    path: '/api/orders/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Order ID' }],
    description: 'Fetches an order with its complete item breakdown and status.',
    sampleBody: null
  },
  {
    id: 'orders-update-status',
    category: 'Orders Management',
    name: 'Update Order Status',
    method: 'PUT',
    path: '/api/orders/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Order ID' }],
    queryParams: [{ key: 'status', default: 'PREPARING', label: 'Status (PLACED/CONFIRMED/PREPARING/OUT_FOR_DELIVERY/DELIVERED/CANCELLED)' }],
    description: 'Updates delivery status of an order.',
    sampleBody: null
  },
  {
    id: 'orders-cancel',
    category: 'Orders Management',
    name: 'Cancel Order by ID',
    method: 'DELETE',
    path: '/api/orders/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Order ID' }],
    description: 'Cancels an existing order.',
    sampleBody: null
  },

  // 12. Payments & Stripe Gateway
  {
    id: 'payments-initiate',
    category: 'Payments & Stripe Gateway',
    name: 'Initiate Stripe Payment Intent',
    method: 'POST',
    path: '/api/payments/initiate',
    description: 'Creates a Stripe PaymentIntent with client secret for card transactions.',
    sampleBody: {
      orderId: 1,
      userId: 1,
      amount: 450.0,
      paymentMethod: "STRIPE"
    }
  },
  {
    id: 'payments-create',
    category: 'Payments & Stripe Gateway',
    name: 'Create Payment Record',
    method: 'POST',
    path: '/api/payments',
    description: 'Records a direct payment transaction (UPI, Card, or COD).',
    sampleBody: {
      transactionId: "TXN_UPI_992138",
      orderId: 1,
      userId: 1,
      amount: 450.0,
      paymentMethod: "UPI",
      paymentStatus: "SUCCESS"
    }
  },
  {
    id: 'payments-get-all',
    category: 'Payments & Stripe Gateway',
    name: 'Get All Payments',
    method: 'GET',
    path: '/api/payments',
    description: 'Fetches all payment ledger transactions.',
    sampleBody: null
  },
  {
    id: 'payments-get-by-id',
    category: 'Payments & Stripe Gateway',
    name: 'Get Payment by ID',
    method: 'GET',
    path: '/api/payments/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Payment ID' }],
    description: 'Fetches a single payment record.',
    sampleBody: null
  },
  {
    id: 'payments-update-status',
    category: 'Payments & Stripe Gateway',
    name: 'Update Payment Status',
    method: 'PUT',
    path: '/api/payments/{id}/status',
    pathParams: [{ key: 'id', default: '1', label: 'Payment ID' }],
    queryParams: [{ key: 'status', default: 'PAID', label: 'Status (PAID/FAILED/PENDING/REFUNDED)' }],
    description: 'Updates payment authorization status.',
    sampleBody: null
  },
  {
    id: 'payments-delete',
    category: 'Payments & Stripe Gateway',
    name: 'Delete Payment by ID',
    method: 'DELETE',
    path: '/api/payments/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Payment ID' }],
    description: 'Deletes a payment record.',
    sampleBody: null
  },
  {
    id: 'payments-stripe-webhook',
    category: 'Payments & Stripe Gateway',
    name: 'Simulate Stripe Webhook Event',
    method: 'POST',
    path: '/api/webhooks/stripe',
    description: 'Dispatches simulated Stripe webhook event (payment_intent.succeeded or failed) to settle payments.',
    sampleBody: {
      id: "evt_test_mock_001",
      object: "event",
      type: "payment_intent.succeeded",
      data: {
        object: {
          id: "pi_test_mock_123456",
          amount: 45000,
          currency: "inr",
          status: "succeeded"
        }
      }
    }
  },

  // 13. Feedback & Reviews
  {
    id: 'feedback-create',
    category: 'Customer Feedback & Reviews',
    name: 'Create Feedback / Review',
    method: 'POST',
    path: '/api/feedback',
    description: 'Submits user rating and review for restaurant and dish.',
    sampleBody: {
      userId: 1,
      restaurantId: 1,
      foodItemId: 1,
      rating: 5,
      comment: "Delicious food, hot delivery, and wonderful packaging!"
    }
  },
  {
    id: 'feedback-get-all',
    category: 'Customer Feedback & Reviews',
    name: 'Get All Feedback',
    method: 'GET',
    path: '/api/feedback',
    description: 'Fetches all customer reviews.',
    sampleBody: null
  },
  {
    id: 'feedback-get-by-id',
    category: 'Customer Feedback & Reviews',
    name: 'Get Feedback by ID',
    method: 'GET',
    path: '/api/feedback/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Feedback ID' }],
    description: 'Fetches single feedback entry.',
    sampleBody: null
  },
  {
    id: 'feedback-get-by-restaurant',
    category: 'Customer Feedback & Reviews',
    name: 'Get Feedback by Restaurant ID',
    method: 'GET',
    path: '/api/feedback/restaurant/{restaurantId}',
    pathParams: [{ key: 'restaurantId', default: '1', label: 'Restaurant ID' }],
    description: 'Fetches all reviews for a specific restaurant.',
    sampleBody: null
  },
  {
    id: 'feedback-update',
    category: 'Customer Feedback & Reviews',
    name: 'Update Feedback',
    method: 'PUT',
    path: '/api/feedback/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Feedback ID' }],
    description: 'Updates a feedback entry.',
    sampleBody: {
      userId: 1,
      restaurantId: 1,
      foodItemId: 1,
      rating: 5,
      comment: "Updated review: Absolutely loved the meal! Will order again."
    }
  },
  {
    id: 'feedback-delete',
    category: 'Customer Feedback & Reviews',
    name: 'Delete Feedback by ID',
    method: 'DELETE',
    path: '/api/feedback/{id}',
    pathParams: [{ key: 'id', default: '1', label: 'Feedback ID' }],
    description: 'Deletes a review record.',
    sampleBody: null
  }
]
